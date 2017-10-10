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
		long start = System.currentTimeMillis();
		String[] libs = new String[] { "" };
//		String inpath = "F:/Study/Research/RNN/TypeResolutionParser-master/TypeResolutionParser-master/src/testInput";
//		String inpath = "F:\\Study\\joda-time-master\\src";
//		String inpath = "F:\\Study\\Research\\GraphModelForArgumentRecommendation\\eclipse.jdt.core-master\\eclipse.jdt.core-master";
		String inpath = "F:\\Study\\Research\\RNN\\Dataset\\test";
//		String inpath = "/data";
		String outpath = "F:\\Study\\Research\\RNN\\sequences\\testDataSet";
		CorpusGenerator cg = new CorpusGenerator(inpath);
		cg.generateSequences(inpath, outpath);
		long end = System.currentTimeMillis();
		System.out.println("Finish parsing corpus in " + (end - start));
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
						inDir.getAbsolutePath()+"\\", true);
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
}
