package org.doraemon.treasure.ares.sequence.dao;

import org.doraemon.treasure.ares.sequence.SequenceException;
import org.doraemon.treasure.ares.sequence.seq.SequenceRange;

import javax.sql.DataSource;

public interface SequenceDao {
	
	SequenceRange nextRange(String name, int index, int total) throws SequenceException;

	void setStep(int step);
	
	void setDataSource(DataSource dataSource);
}
