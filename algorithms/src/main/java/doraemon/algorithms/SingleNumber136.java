package doraemon.algorithms;

import java.util.Arrays;

public class SingleNumber136 {

    public static int singleNumber(int[] nums) {
        // Set<Integer> s= new HashSet<>();
        // for(int i : nums){
        //     if(s.contains(i))s.remove(i);
        //     else s.add(i);
        // }
        // return s.iterator().next();
        return Arrays.stream(nums).reduce(0, (a, b) -> a ^ b);
    }

    public static void main(String[] args) {
        int a[] = {1, 2, 3, 4, 3, 2, 4};
        System.out.println(singleNumber(a));
    }
}
