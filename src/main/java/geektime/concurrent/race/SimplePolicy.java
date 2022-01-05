package geektime.concurrent.race;

import io.netty.util.concurrent.DefaultThreadFactory;

import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class SimplePolicy {
	
	final int genThreadInPool = 4; //不超过8
	final int computeThreadInPool = 8; //不超过16
	SimpleShareData ssd;

	private ThreadPoolExecutor genThreadPool;
	private ThreadPoolExecutor computeThreadPool;
	
	public SimplePolicy() {
		ssd = new SimpleShareData();
		ssd.initGenSignals(genThreadInPool);
		ssd.initCompSignals(computeThreadInPool);
		ssd.initExchange();
		initThreads();
	}

	private void initThreads() {
		ThreadFactory factory = Executors.defaultThreadFactory();
		genThreadPool = new ThreadPoolExecutor(genThreadInPool, genThreadInPool, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), factory);
		computeThreadPool = new ThreadPoolExecutor(computeThreadInPool, computeThreadInPool, 0L, TimeUnit.MILLISECONDS, new SynchronousQueue<>(), factory);
	}

	public long go() throws Exception {
		//使用自定义方法A计算
		long startTime = System.currentTimeMillis();
		System.out.println("开始自定义A方法计算计时: " + startTime);
		

		for (int tp = 0; tp < genThreadInPool; tp++) {
			SimpleSyncGen simpleSyncGen = new SimpleSyncGen(ssd, SimpleShareData.COUNT / genThreadInPool, tp);
			genThreadPool.execute(simpleSyncGen);
		}
		
		ssd.getGenSig().await();

		//System.out.println("随机数个数为：" + ssd.getScore().size());
		long genTime = System.currentTimeMillis();
		System.out.println("产生随机数时长: " + (genTime - startTime));

		for (int tp = 0; tp < computeThreadInPool; tp++) {
			SimpleDivideCompute simpleDiv = new SimpleDivideCompute(ssd, SimpleShareData.COUNT / computeThreadInPool, tp);
			computeThreadPool.execute(simpleDiv);
		}
		ssd.getCompSig().await();
		
		List<Integer> box = ssd.getShare().parallelStream().parallel().sorted().collect(Collectors.toList());
		for (int i = 0; i < SimpleShareData.BUFSIZE; i++) {
			ssd.getTop()[i] = box.get(box.size() - i - 1);
		}
		printTop();
		
		long sortTime = System.currentTimeMillis();
		System.out.println("自定义A方法计算时长: " + (sortTime - genTime));
		
		computeThreadPool.shutdown();
		genThreadPool.shutdown();
		return sortTime - startTime;
	}
    void printTop() {
    	System.out.println("前10成绩为:");
    	for (int j = 0; j <= 10; j++) {
    		System.out.print(ssd.getTop()[j] + " ");
    	}
    	System.out.println();
    }
}
