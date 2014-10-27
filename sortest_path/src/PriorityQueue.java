public class PriorityQueue {

	private HeapNode[] minHeap;
	public int maxSize;
	public int size;
	
	public class HeapNode {
		int elem;
		int priority;
		HeapNode(int elem, int priority) {
			this.elem = elem;
			this.priority = priority;
		}
	}

	public PriorityQueue(int numNodes) {
		maxSize = numNodes;
		minHeap = new HeapNode[maxSize];
		size = 0;
		minHeap[0] = new HeapNode(Integer.MIN_VALUE, Integer.MIN_VALUE);
	}

	private int leftchild(int pos) {
		return 2 * pos;
	}

/*	private int rightchild(int pos) {
		return 2 * pos + 1;
	}*/

	private int parent(int position) {
		return position/2;
	}

	private boolean isLeaf(int position) {
		return position>size/2 && position<=size;
	}

	private void swap(int pos1, int pos2) {
		HeapNode temp = minHeap[pos1];
		minHeap[pos1] = minHeap[pos2];
		minHeap[pos2] = temp;
	}

	public void insert(int elem, int priority) {
		size += 1;
		HeapNode node = new HeapNode(elem, priority);
		minHeap[size] = node;
		int current = size;
		while (minHeap[current].priority < minHeap[parent(current)].priority) {
			swap(current,parent(current));
			current = parent(current);
		}
	}

/*	public void print() {
		int i;
		for (i = 1; i <= size; i++) {
			System.out.print(minHeap[i] + " ");
		}
		System.out.println();
	}*/

	public int removemin() {
		swap(1, size);
		size -= 1;
		if (size!=0) {
			pushdown(1);
		}
		return minHeap[size + 1].elem;
	}

	private void pushdown(int position) {
		int smallestchild;
		while (!isLeaf(position)) {
			smallestchild = leftchild(position);
			if (smallestchild < size && minHeap[smallestchild].priority > minHeap[smallestchild + 1].priority) {
				smallestchild += 1;
			}
			if (minHeap[position].priority <= minHeap[smallestchild].priority) {
				return;
			}
			swap(position,smallestchild);
			position = smallestchild;
		}
	}

	public void reduceKey(int elem, int priority) {
		for (int i=1; i<this.size; i++){
			if (minHeap[i].elem==elem){
				minHeap[i].priority = priority;
				pushdown(1);
				break;
			}
		}
	}

	public boolean contains(int elem) {
		for (int i = 1; i < this.size; i++) {
			if (minHeap[i].elem == elem) {
				return true;
			}
		}
		return false;
	}


}