package org.doraemon.treasure.ares.sequence.seq;

import org.doraemon.treasure.ares.sequence.SequenceException;
import org.doraemon.treasure.ares.sequence.dao.SequenceDao;

public interface Sequence {

    long nextValue(String keyName,int index, int total) throws SequenceException;

    void setKeyName(String name);

    void setSequenceDao(SequenceDao sequenceDao);

    SequenceDao getSequenceDao();
}
