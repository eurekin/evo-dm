/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package testrunner;

import EvolutionaryAlgorithm.Configuration;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author eurekin
 */
class JobConfiguration {

    private synchronized static void addJob(JobConfiguration jc) {
        jobs.add(jc);
    }

    private synchronized void removeJob(JobConfiguration aThis) {
        jobs.remove(aThis);
    }

    protected Set<JobConfiguration> jobSet;
    public static final String PROP_JOBSET = "jobSet";

    public Set<JobConfiguration> getJobSet() {
        return jobSet;
    }

    public void setJobSet(Set<JobConfiguration> jobSet) {
        Set<JobConfiguration> oldJobSet = this.jobSet;
        this.jobSet = jobSet;
        propertyChangeSupport.firePropertyChange(PROP_JOBSET, oldJobSet, jobSet);
    }
    private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.removePropertyChangeListener(listener);
    }



    FileLock lock;
    FileChannel channel;
    Configuration configuration;
    File file;
    private static Set<JobConfiguration> jobs = new CopyOnWriteArraySet<JobConfiguration>();

    public Configuration getConfiguration() {
        return configuration;
    }

    public JobConfiguration(File f,
            FileLock lock, FileChannel channel, Configuration configuration) {
        this.lock = lock;
        this.channel = channel;
        this.configuration = configuration;
        this.file = f;
        Configuration.setConfiguration(configuration);
        jobs.add(this);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final JobConfiguration other = (JobConfiguration) obj;
        if (this.configuration != other.configuration && (this.configuration == null || !this.configuration.equals(other.configuration)))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + (this.configuration != null ? this.configuration.hashCode() : 0);
        return hash;
    }

    public static JobConfiguration tryToAcquire(File file) {
        if (file.length() == 0L) {
            file.delete();
            return null;
        }
        RandomAccessFile raf = null;
        FileChannel channel = null;
        try {
            raf = new RandomAccessFile(file, "rw");
            channel = raf.getChannel();
            FileLock lock = channel.lock();

            ByteBuffer bb = ByteBuffer.allocate((int) channel.size());
            channel.read(bb);
            ByteArrayInputStream in = new ByteArrayInputStream(bb.array());
            ObjectInputStream ois = new ObjectInputStream(in);
            Configuration result = (Configuration) ois.readObject();
            ois.close();

            JobConfiguration jc = new JobConfiguration(file, lock, channel, result);
            addJob(jc);
            return jc;
        } catch (Exception e) {
            if (!(e instanceof OverlappingFileLockException))
                e.printStackTrace();
            else if (raf != null)
                try {
                    if (channel != null)
                        channel.close();
                    if (raf != null)
                        raf.close();
                } catch (IOException ex) {
                    Logger.getLogger(JobConfiguration.class.getName()).log(Level.SEVERE, null, ex);
                }
            return null;
        }
    }

    public void releaseAndDestroy() {
        try {
            file.delete();
            lock.release();
            channel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        removeJob(this);
    }

    public void release() {
        try {
            lock.release();
            channel.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        removeJob(this);
    }

}
