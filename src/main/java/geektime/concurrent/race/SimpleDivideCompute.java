package geektime.concurrent.race;

import java.util.List;
import java.util.stream.Collectors;

public class SimpleDivideCompute implements ComputeRunnable {

	SimpleShareData ssd;
	int size;
	int offset;
	public SimpleDivideCompute(SimpleShareData ssd, int size, int offset) {
		this.ssd = ssd;
		this.size = size;
		this.offset = offset;
	}
	
	@Override
	public void run() {
		go();

	}

	@Override
	public void go() {
		// System.out.println("开始计算随机数: " + size + " " + offset);
		List<Integer> alist = ssd.getScore().subList(offset * size, offset * size + size)
				.parallelStream().sorted().collect(Collectors.toList());
		
		for (int i = 0; i < SimpleShareData.BUFSIZE * size / SimpleShareData.COUNT; i++) {
			//System.out.println("随机数: " + alist[alist.length - i - 1]);
			ssd.addExchange(alist.get(alist.size() - i - 1));
		}
		ssd.getCompSig().countDown();
		//System.out.println("计算随机数完毕: " + size + " " + SimpleShareData.BUFSIZE * size / SimpleShareData.COUNT);
	}
}
