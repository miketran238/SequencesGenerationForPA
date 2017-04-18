package parser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Scanner;

import utils.FileUtil;

public class CorpusGenerator {
	private String repoPath;

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		long start = System.currentTimeMillis();
		String[] libs = new String[] { "org.apache.commons.", "android.",
				"com.google.gwt.", "org.hibernate.", "org.joda.time.",
				"com.thoughtworks.xstream." };
		CorpusGenerator cg = new CorpusGenerator(
				"C:\\Users\\pdhung\\Documents\\GitHub\\candlepin\\");
		cg.generateSequences("C:\\Users\\pdhung\\Documents\\GitHub\\candlepin\\", "C:\\Users\\pdhung\\Desktop\\hungData\\research\\ImportantProjects\\SpecMiningProject\\ParameterRecommendation\\output_sequences\\");
		long end = System.currentTimeMillis();
		System.out.println("Finish parsing corpus in " + (end - start) / 1000);
	}

	public CorpusGenerator(String repoPath) {
		this.repoPath = repoPath;
	}

	public void generateSequences(final String repoListsPath,
			final String outPath) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				File outDir=new File(outPath);
				File inDir=new File(repoListsPath);
				ProjectSequencesGenerator psg = new ProjectSequencesGenerator(
						inDir.getAbsolutePath()+"\\", false);
				if (!outDir.exists())
					outDir.mkdirs();
				try {
					int n = psg.generateSequences(false, null,
							outDir.getAbsolutePath());
				} catch (Throwable t) {
					System.err.println("Error in parsing " 
							+ " project ");
					t.printStackTrace();
				}			
			}
		}).start();

	}

	/**
	 * 
	 * @param inPath
	 * @param doVerify
	 * @return numbers[0]: number of project with different numbers of
	 *         sequences; numbers[1]: number of sequences with different
	 *         lengths; numbers[2]: number of sequences with non-aligned tokens;
	 *         numbers[3]: number of non-aligned tokens
	 */
	public static int[] concatSequences(String inPath, String outPath,
			boolean keepNonAlignment) {
		int[] numbers = new int[] { 0, 0, 0, 0 };
		PrintStream sources = null, targets = null;
		new File(outPath).mkdirs();
		try {
			sources = new PrintStream(new FileOutputStream(outPath
					+ "/source.txt"));
			targets = new PrintStream(new FileOutputStream(outPath
					+ "/target.txt"));
		} catch (IOException e) {
			return null;
		}
		File dir = new File(inPath);
		for (File sublib : dir.listFiles()) {
			for (File subp : sublib.listFiles()) {
				ArrayList<String> sourceSequences = FileUtil
						.getFileStringArray(subp.getAbsolutePath()
								+ "/source.txt"), targetSequences = FileUtil
						.getFileStringArray(subp.getAbsolutePath()
								+ "/target.txt");
				if (sourceSequences.size() != targetSequences.size()) {
					numbers[0]++;
					continue;
				}
				for (int i = 0; i < sourceSequences.size(); i++) {
					String source = sourceSequences.get(i), target = targetSequences
							.get(i);
					String[] sTokens = source.trim().split(" "), tTokens = target
							.trim().split(" ");
					if (sTokens.length != tTokens.length) {
						numbers[1]++;
						if (!keepNonAlignment)
							continue;
					}
					if (!keepNonAlignment) {
						boolean aligned = true;
						for (int j = 0; j < sTokens.length; j++) {
							String s = sTokens[j], t = tTokens[j];
							if ((t.contains(".") && !t.endsWith(s))
									|| (!t.contains(".") && !t.equals(s))) {
								aligned = false;
								numbers[3]++;
							}
						}
						if (!aligned) {
							numbers[2]++;
							if (!keepNonAlignment)
								continue;
						}
					}
					sources.println(source);
					targets.println(target);
				}
			}
		}
		sources.flush();
		targets.flush();
		sources.close();
		targets.close();
		return numbers;
	}

}
