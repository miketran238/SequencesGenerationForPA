package data;

public class TestData 
{
	int[] nums;
	
	public TestData(int[] numsIn)
	{
		boolean a = false;
		assert !a;
		nums = numsIn;
	}
	
	public void quickSort(int l, int h)
	{
		int pivotIndex = l;
		if((h-l) < 1)
		{
			return;
		}
		int pivot = nums[l];

		for (int i = l+1; i <= h; i++)
		{
			if(nums[i] < pivot)
			{
				//Swap
				int temp = nums[pivotIndex];
				nums[pivotIndex] = nums[i];
				nums[i] = temp;
				pivotIndex  = i ;
			}
		}
		for(Integer i: nums)
		{
			System.out.print(i + ",");
		}
		System.out.print("l,h,pivot" + l + "," + h + "," + pivotIndex);
		System.out.println();
		quickSort(l,pivotIndex-1);
		quickSort(pivotIndex+1,h);
		
		return;
		
	}
}

