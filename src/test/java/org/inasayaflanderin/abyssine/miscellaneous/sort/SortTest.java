package org.inasayaflanderin.abyssine.miscellaneous.sort;

import net.jqwik.api.*;
import org.inasayaflanderin.abyssine.miscellaneous.RandomAccessUtils;

import java.util.Comparator;

import static org.junit.jupiter.api.Assertions.assertTrue;

class SortTest {
    @Provide
    Arbitrary<Double[]> provider() {
        return Arbitraries.doubles().list().ofMinSize(10).ofMaxSize(1000).map(list -> list.toArray(new Double[0]));
    }

    @Property
    void selectionTest(@ForAll("provider") Double[] array) {
        Sort.selection(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void doubleSelectionTest(@ForAll("provider") Double[] array) {
        Sort.doubleSelection(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void insertionTest(@ForAll("provider") Double[] array) {
        Sort.insertion(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void binaryInsertionTest(@ForAll("provider") Double[] array) {
        Sort.binaryInsertion(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void bubbleTest(@ForAll("provider") Double[] array) {
        Sort.bubble(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void shakerTest(@ForAll("provider") Double[] array) {
        Sort.shaker(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void quickTest(@ForAll("provider") Double[] array) {
        Sort.quick(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void mergeTest(@ForAll("provider") Double[] array) {
        Sort.merge(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void heapTest(@ForAll("provider") Double[] array) {
        Sort.heap(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void combTest(@ForAll("provider") Double[] array) {
        Sort.comb(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void shellTest(@ForAll("provider") Double[] array) {
        Sort.shell(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void cycleTest(@ForAll("provider") Double[] array) {
        Sort.cycle(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void patienceTest(@ForAll("provider") Double[] array) {
        Sort.patience(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void exchangeTest(@ForAll("provider") Double[] array) {
        Sort.exchange(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void oddEvenTest(@ForAll("provider") Double[] array) {
        Sort.oddEven(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void circleTest(@ForAll("provider") Double[] array) {
        Sort.circle(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void mergeInsertionTest(@ForAll("provider") Double[] array) {
        Sort.mergeInsertion(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void mergeInsertion2Test(@ForAll("provider") Double[] array) {
        Sort.mergeInsertion2(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void tournamentTest(@ForAll("provider") Double[] array) {
        Sort.tournament(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void treeTest(@ForAll("provider") Double[] array) {
        Sort.tree(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void gnomeTest(@ForAll("provider") Double[] array) {
        Sort.gnome(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void strandTest(@ForAll("provider") Double[] array) {
        Sort.strand(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void bitonicNetworkTest(@ForAll("provider") Double[] array) {
        Sort.bitonicNetwork(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void oddEvenNetworkTest(@ForAll("provider") Double[] array) {
        Sort.oddEvenNetwork(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void quickLLTest(@ForAll("provider") Double[] array) {
        Sort.quickLL(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void dualPivotQuickTest(@ForAll("provider") Double[] array) {
        Sort.dualPivotQuick(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void introTest(@ForAll("provider") Double[] array) {
        Sort.intro(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void timTest(@ForAll("provider") Double[] array) {
        Sort.tim(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void weaveTest(@ForAll("provider") Double[] array) {
        Sort.weave(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void smoothTest(@ForAll("provider") Double[] array) {
        Sort.smooth(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void cartesianTest(@ForAll("provider") Double[] array) {
        Sort.cartesian(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void sqrtTest(@ForAll("provider") Double[] array) {
        Sort.sqrt(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }

    @Property
    void wikiTest(@ForAll("provider") Double[] array) {
        Sort.wiki(array, Comparator.naturalOrder());
        assertTrue(RandomAccessUtils.isSort(array, Comparator.naturalOrder()));
    }
}