package data;

public class TestData 
{
	int[] nums;
	
	public TestData(int[] numsIn)
	{
		boolean a = false;
		assert !a;
		nums = numsIn;
		testCastExp();
	}
	
	//test array access, array creation and array intializer
	public void testArray()
	{
		int[] a = new int[5];
		int[] b = new int[]{1,2,3};
		a[0] = 100;
	}
	
	public void testCastExp()
	{
		int a = (int) 1.1; 
		Object o = null;
		TestData td = (TestData) o;
		System.out.println(a);
	}
	
	//class intance creation and constructor invocation
	public void testClassCreation()
	{
		TestData dt = new TestData(new int[]{1,2,3});
	}
	
	public void testFieldAccess()
	{
		this.nums[0] = 1;
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

