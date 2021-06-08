package com.marklogic.client.ext.batch;

import com.marklogic.client.DatabaseClient;
import com.marklogic.client.datamovement.DataMovementManager;
import com.marklogic.client.datamovement.JobTicket;
import com.marklogic.client.datamovement.WriteBatcher;
import com.marklogic.client.document.DocumentWriteOperation;

import java.util.List;

public class DataMovementBatchWriter extends BaseBatchWriter implements BatchWriter {

	private DataMovementManager dataMovementManager;
	private WriteBatcher writeBatcher;
	private int batchSize = 100;
	private int threadCount = 10;
	private JobTicket jobTicket;

	public DataMovementBatchWriter(DatabaseClient client) {
		this.dataMovementManager = client.newDataMovementManager();
	}

	@Override
	public void initialize() {
		writeBatcher = this.dataMovementManager.newWriteBatcher();
		writeBatcher.withThreadCount(threadCount);
		writeBatcher.withBatchSize(batchSize);
		jobTicket = dataMovementManager.startJob(writeBatcher);
	}

	@Override
	public void write(List<? extends DocumentWriteOperation> items) {
		for (DocumentWriteOperation op : items) {
			writeBatcher.add(op.getUri(), op.getMetadata(), op.getContent());
		}
	}

	@Override
	public void waitForCompletion() {
		if (writeBatcher != null) {
			writeBatcher.flushAndWait();
			super.stop();
			if (jobTicket != null) {
				dataMovementManager.stopJob(writeBatcher);
			} else {
				dataMovementManager.stopJob(writeBatcher);
			}
		}
	}

	public void setBatchSize(int batchSize) {
		this.batchSize = batchSize;
	}

	public void setThreadCount(int threadCount) {
		this.threadCount = threadCount;
	}
}
