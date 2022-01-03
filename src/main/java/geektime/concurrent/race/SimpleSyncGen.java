package geektime.concurrent.race;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SimpleSyncGen implements GenRunnable {

	SimpleShareData ssd;
	int size;
	int offset;
	private ReadWriteLock lock = new ReentrantReadWriteLock();
	public SimpleSyncGen(SimpleShareData ssd, int size, int offset) {
		this.ssd = ssd;
		this.size = size;
		this.offset = offset;
	}
	
	@Override
	public void run() {
		gen();
	}

	@Override
	public void gen() {
		//System.out.println("开始产生随机数: " + size);
		Random rand = new Random(System.currentTimeMillis());
		int r;
		int i;
    	for (i = 0; i < size; i++) {
    		r = rand.nextInt(SimpleShareData.COUNT);
    		synchronized (ssd.getScore()) {
    			ssd.getScore().add(new Integer(r));
    		}
    	}
    	//System.out.println("产生随机数个数: " + i);
    	ssd.getGenSig().countDown();
	}

}
