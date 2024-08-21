package org.inasayaflanderin.abyssine;

import java.util.Arrays;
import java.util.Random;

/*TODO implement these
* List of sort not include here:
* Radix LSD
* Radix MSD
* Bucket
* Counting
*
* Spaghetti
* Gravity
* Pancake
*
* Bogo
* Stooge
* Slow
* Quantum Bogo
* Stalin
* Sleep
* Miracle
* Bogobogo
* Power
*
* List of sort will include here
* Cycle
* Patient
* Exchange
* Odd-Even
* Circle
*
* Merge-Insertion
* Tournament
* Tree
* Gnome
* Library
* Strand
* Topological
*
* (this one might in another place)
* Sorting network - Bitonic - Odd-Even - Pairwise
*
* Quick LL
* Dual Pivot Quick
* Porportion extends
* Intro
* pattern Defeating
*
* Tim
*
* Iterative Merge
* Naive In Place Merge
* Weave
* Rotate Merge
* Quad
*
* Weak Heap
* Smooth
* Poplar
* Ternary
*
* LSD Shift
* MSD Cycle
*
* Binary Quick
* American Flag
* Burst
* spread
* Sample
* Proxmap
*
* Cartesian
*
* Sqrt
* Block
* Wiki
* Grail
* */

public class ArraySort {
   public static final int ASCENDING = 1;
   public static final int DESCENDING = 2;

   public static <E extends Comparable<E>> void selection(E[] array, int order) {
        for(int i = 0; i < array.length; i++) {
            int swapIndex = i;

            for(int j = i + 1; j < array.length; j++) swapIndex = ((order == 1) ? (array[j].compareTo(array[swapIndex]) < 0) : (array[j].compareTo(array[swapIndex]) > 0)) ? j : swapIndex;

            if(i != swapIndex) swap(array, i, swapIndex);
        }
   }

   public static <E extends Comparable<E>> void doubleSelection(E[] array, int order) {
       for (int i = 0; i < array.length / 2; i++) {
           var leftIndex = i;
           var rightLimit = array.length - 1 - i;
           var rightIndex = rightLimit;

           for (int j = i; j <= rightLimit; j++) {
               if((order == 1) ? (array[j].compareTo(array[leftIndex]) < 0) : (array[j].compareTo(array[leftIndex]) > 0)) leftIndex = j;
               if((order == 1) ? (array[j].compareTo(array[rightIndex]) > 0) : (array[j].compareTo(array[rightIndex]) < 0)) rightIndex = j;
           }

           if(i != leftIndex) swap(array, i, leftIndex);
           if(rightLimit != rightIndex) swap(array, rightLimit, rightIndex);
       }
   }

   public static <E extends Comparable<E>> void insertion(E[] array, int order) {
       for (int i = 1; i < array.length; ++i) {
           E datum = array[i];
           var j = i - 1;

           while(j >= 0 && ((order == 1) ? array[j].compareTo(datum) > 0 : array[j].compareTo(datum) < 0)) {
               array[j + 1] = array[j];
               j--;
           }

           array[j + 1] = datum;
       }
   }

   public static <E extends Comparable<E>> void binaryInsertion(E[] array, int order) {
       for (int i = 1; i < array.length; i++) {
           E datum = array[i];
           int j = Arrays.binarySearch(array, 0, i, datum, (a, b) -> order == 1 ? a.compareTo(b) : b.compareTo(a));

           if(j < 0) j = -j- 1;
           System.arraycopy(array, j, array, j + 1, i - j);

           array[j] = datum;
       }
   }

   public static <E extends Comparable<E>> void bubble(E[] array, int order) {
       for (int i = 0; i < array.length - 1; i++) {
           var swapped = false;

           for (int j = 0; j < array.length - 1 - i; j++) {
               if((order == 1) ? (array[j].compareTo(array[j + 1]) > 0) : (array[j].compareTo(array[j + 1]) < 0)) {
                   swap(array, j, j + 1);
                   swapped = true;
               }
           }

           if(!swapped) break;
       }
   }

    public static <E extends Comparable<E>> void shaker(E[] array, int order) {
       var left = 0;
       var right = array.length - 1;
       var k = right;

       while(left < right) {
           var swapped = false;

           for(int i = left; i < right; i++) {
                if((order == 1) ? (array[i].compareTo(array[i + 1]) > 0) : (array[i].compareTo(array[i + 1]) < 0)) {
                    swap(array, i, i + 1);
                    k = i;
                    swapped = true;
                }
           }

           if(!swapped) break;
           else swapped = false;

           right = k;

           for(int i = right; i > left; i--) {
               if((order == 1) ? (array[i].compareTo(array[i - 1]) < 0) : (array[i].compareTo(array[i - 1]) > 0)) {
                   swap(array, i, i - 1);
                   k = i;
                   swapped = true;
               }
           }

           if(!swapped) break;

           left = k;
       }
    }

    //TODO multithreading quicksort
    public static <E extends Comparable<E>> void quick(E[] array, int order) {
       quicksort(array, 0, array.length - 1, order);
    }

    //TODO multithreading mergesort
    public static <E extends Comparable<E>> void merge(E[] array, int order) {
        mergesort(array, 0, array.length - 1, order);
    }

    public static <E extends Comparable<E>> void heap(E[] array, int order) {
       for(int i = array.length / 2 - 1; i>= 0; i--) heapify(array, array.length, i, order);

       for(int i = array.length - 1; i > 0; i--) {
           swap(array, 0, i);
           heapify(array, i, 0, order);
       }
    }

    public static <E extends Comparable<E>> void comb(E[] array, int order) {
        var swapped = false;
        var gap = array.length;

        while(gap != 1 || swapped) {
            gap = gap * 10 / 13;

            if(gap < 1) gap = 1;

            swapped = false;

            for (int i = 0; i < array.length - gap; i++) {
                if((order == 1) ? array[i].compareTo(array[i + gap]) > 0 : array[i].compareTo(array[i + gap]) < 0) {
                    swap(array, i, i + gap);
                    swapped = true;
                }
            }
        }
    }

    public static <E extends Comparable<E>> void shell(E[] array, int order) {
       for(int gap = array.length * 10 / 23; gap > 0; gap = gap * 10 / 23) {
           for(int i = gap; i < array.length; i++) {
               E datum = array[i];
               int j;

               for(j = i; j >= gap && order == 1 ? array[j - gap].compareTo(datum) > 0 : array[j - gap].compareTo(datum) < 0; j-= gap) array[j] = array[j - gap];

               array[j] = datum;
           }
       }
    }

    private static <E extends Comparable<E>> void quicksort(E[] array, int start, int end, int order) {
       if(start < end) {
            var ran = new Random();
            swap(array, ran.nextInt(end - start + 1) + start, end);

            E pivot = array[end];

            int i = start - 1;

            for (int j = start; j < end; j++) {
               if((order == 1) ? (array[j].compareTo(pivot) < 0) : (array[j].compareTo(pivot) > 0)) {
                   i++;
                   swap(array, i, j);
               }
            }

           swap(array, i + 1, end);
           i++;

           quicksort(array, start, i-1, order);
           quicksort(array, i+1, end, order);
       }
    }

    private static <E extends Comparable<E>> void mergesort(E[] array, int start, int end, int order) {
       if(start < end) {
           var middle = start + (end - start) / 2;
           var leftArraySize = middle - start + 1;
           var i = 0;
           var j = 0;
           var k = start;

           mergesort(array, start, middle, order);
           mergesort(array, middle + 1, end, order);

           E[] leftArray = Arrays.copyOf(array, leftArraySize);
           E[] rightArray = Arrays.copyOfRange(array, leftArraySize, end + 1);

           while (i < leftArray.length && j < rightArray.length) {
               if ((order == 1) ? (leftArray[i].compareTo(rightArray[j]) <= 0) : (leftArray[i].compareTo(rightArray[j]) >= 0)) {
                   array[k] = leftArray[i];
                   i++;
               } else {
                   array[k] = rightArray[j];
                   j++;
               }

               k++;
           }

           while (i < leftArray.length) {
               array[k] = leftArray[i];
               i++;
               k++;
           }

           while(j < rightArray.length) {
               array[k] = rightArray[j];
               j++;
               k++;
           }
       }
    }

    private static <E extends Comparable<E>> void heapify(E[] array, int size, int index, int order) {
        var root = index;
        var left = 2 * index + 1;
        var right = 2 * index + 2;

        if(left < size && (order == 1) ? (array[left].compareTo(array[root]) > 0) : (array[left].compareTo(array[root]) < 0)) root = left;

        if(right < size && (order == 1) ? (array[right].compareTo(array[root]) > 0) : (array[left].compareTo(array[root]) < 0)) root = right;

        if(root != index) {
            swap(array, root, index);

            heapify(array, size, root, order);
        }
    }

    private static <E> void swap(E[] array, int firstIndex, int secondIndex) {
       E temp = array[secondIndex];
       array[secondIndex] = array[firstIndex];
       array[firstIndex] = temp;
    }
}