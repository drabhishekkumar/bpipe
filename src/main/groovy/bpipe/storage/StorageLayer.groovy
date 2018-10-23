package bpipe.storage

import java.nio.file.Path

import bpipe.Config
import bpipe.executor.CommandExecutor
import groovy.transform.CompileStatic

abstract class StorageLayer implements Serializable {
    
    public static final long serialVersionUID = 0L
    
    /**
     * The name of the configuration from which this storage was
     * generated
     */
    String name
    
    abstract boolean exists(String path)
    
    abstract Path toPath(String path)
    
    abstract void mount(CommandExecutor executor)
    
    static StorageLayer create(String name) {
        
        if(name == null || name == 'local')
            return new LocalFileSystemStorageLayer()
        
        ConfigObject storageConfig = 
            bpipe.Config.userConfig['filesystems']
                        .get(name, null)
        
        if(storageConfig == null)
            throw new bpipe.PipelineError(
                "The value ${name} was supplied as storage, but could not be found in your configuration.\n\n" + 
                "Please add a filesystems entry to your bpipe.config file with an entry for ${name}")
            
        String className = "bpipe.storage." + storageConfig.type + "StorageLayer"
        
        StorageLayer result = Class.forName(className).newInstance()
        result.name = name 
        
        for(kvp in storageConfig) {
            if(result.hasProperty(kvp.key))
                result[kvp.key] = storageConfig[kvp.key]
        }
        return result
    }
    
    private static StorageLayer defaultStorage
    
    static StorageLayer getDefaultStorage() {
        if(defaultStorage == null)
            defaultStorage = create(Config.userConfig.get('storage',null))
        return defaultStorage
    }
}
