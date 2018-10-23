package bpipe.storage

import java.nio.file.Path

import bpipe.PipelineFile
import bpipe.executor.CommandExecutor
import groovy.transform.CompileStatic

class UnknownStoragePipelineFile extends PipelineFile {

    UnknownStoragePipelineFile(String thePath) {
        super(thePath, new StorageLayer() {
            public boolean exists(String path) {
                true
            }
            
            public Path toPath(String path) {
                new File(path).toPath()
            }

            @Override
            public void mount(CommandExecutor executor) {
            }
        })
    }
}
