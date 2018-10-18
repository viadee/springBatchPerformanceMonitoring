/**
 * Copyright � 2016, viadee Unternehmensberatung AG
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, this
 *   list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright notice,
 *   this list of conditions and the following disclaimer in the documentation
 *   and/or other materials provided with the distribution.
 *
 * * Neither the name of the copyright holder nor the names of its
 *   contributors may be used to endorse or promote products derived from
 *   this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package de.viadee.spring.batch.persistence;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import de.viadee.spring.batch.infrastructure.LoggingWrapper;
import de.viadee.spring.batch.persistence.types.SBPMChunkExecution;

/**
 * This class holds a ThreadSafe Queue containing ChunkExecution Objects that shall be stored in the Database.
 * 
 * Whenever a SpbmChunkExecution Object shall be persisted into the Database, it is pushed into this List.
 * 
 * The DatabaseScheduledWriter takes care of emptying this list and persisting the Entrys into the Database.
 * 
 * See DatabaseScheduledWriter class for further Details.
 * 
 */
@Component
public class SBPMChunkExecutionQueue {

    private final Queue<SBPMChunkExecution> chunkExecutionQueue = new ConcurrentLinkedQueue<SBPMChunkExecution>();

    private static final Logger LOG = LoggingWrapper.getLogger(SBPMChunkExecutionQueue.class);

    public synchronized void addChunkExecution(final SBPMChunkExecution sPBMChunkExecution) {
        this.chunkExecutionQueue.add(sPBMChunkExecution);
    }

    public SBPMChunkExecution getChunk() {
        final SBPMChunkExecution chunkExecution = chunkExecutionQueue.poll();
        if (chunkExecution == null) {
            LOG.trace("EMPTY POLL - Chunk Queue is empty");
        }
        return chunkExecution;
    }
}
