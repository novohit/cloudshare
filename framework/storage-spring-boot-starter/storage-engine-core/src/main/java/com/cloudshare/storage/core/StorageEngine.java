package com.cloudshare.storage.core;

import com.cloudshare.storage.core.model.DeleteContext;
import com.cloudshare.storage.core.model.MergeChunkContext;
import com.cloudshare.storage.core.model.StoreChunkContext;
import com.cloudshare.storage.core.model.StoreContext;

import java.io.IOException;

/**
 * @author novo
 * @since 2023/10/10
 */
public interface StorageEngine {

    void store(StoreContext context) throws IOException;

    void delete(DeleteContext context) throws IOException;

    void storeChunk(StoreChunkContext context) throws IOException;

    void mergeChunk(MergeChunkContext context) throws IOException;
}
