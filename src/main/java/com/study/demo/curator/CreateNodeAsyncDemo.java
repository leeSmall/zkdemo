package com.study.demo.curator;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.api.BackgroundCallback;
import org.apache.curator.framework.api.CuratorEvent;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.apache.zookeeper.CreateMode;

/**
* 
* @Description: curator异步创建临时节点
* @author leeSmall
* @date 2018年9月2日
*
*/
public class CreateNodeAsyncDemo {
	static CountDownLatch cdl = new CountDownLatch(2);
	static ExecutorService es = Executors.newFixedThreadPool(2);

	public static void main(String[] args) throws Exception {
		String path = "/zk-curator";
		CuratorFramework client = CuratorFrameworkFactory.builder().connectString("192.168.152.130:2181")
				.sessionTimeoutMs(5000).retryPolicy(new ExponentialBackoffRetry(1000, 3)).build();
		client.start();
		
		//创建临时节点
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			//回调事件处理
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("event code: " + event.getResultCode() + ", type: " + event.getType());
				cdl.countDown();
			}
		}, es).forPath(path, "test".getBytes());

		//创建临时节点
		client.create().creatingParentsIfNeeded().withMode(CreateMode.EPHEMERAL).inBackground(new BackgroundCallback() {
			
			public void processResult(CuratorFramework client, CuratorEvent event) throws Exception {
				System.out.println("event code: " + event.getResultCode() + ", type: " + event.getType());
				cdl.countDown();
			}
		}).forPath(path, "test".getBytes());

		cdl.await();
		es.shutdown();
	}
}
