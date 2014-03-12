package org.jenkinsci.test.acceptance.resolver;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import org.jenkinsci.test.acceptance.controller.Machine;
import org.jenkinsci.test.acceptance.controller.Ssh;

import java.io.File;
import java.io.IOException;

/**
 * Uploads war from local to remote
 *
 * @author Vivek Pandey
 * @author Kohsuke Kawaguchi
 */
public class JenkinsUploader implements JenkinsResolver {
    File war;

    @Inject
    @Named("jenkins_md5_sum")
    private String jenkinsMd5Sum;


    @Inject
    public JenkinsUploader(@Named("jenkins-war-location") File war) {
        this.war = war;
        if(!this.war.exists()){
            throw new AssertionError("Jenkins war file location "+war+" does not exist");
        }
    }

    @Override
    public void materialize(Machine machine, String path) {
        try {
            Ssh ssh = machine.connect();
            File target = new File(path);
            if(!JenkinsDownloader.jenkinsWarExists(ssh.getConnection(),path,jenkinsMd5Sum)){
                ssh.copyTo(war.getPath(), target.getName(), target.getParent());
            }
        } catch (IOException e) {
            throw new AssertionError("Failed to copy "+war+" into "+path,e);
        }
    }

    @Override
    public String materialize(Machine machine) {
        materialize(machine,JENKINS_WAR_TARGET_LOCATION);
        return JENKINS_WAR_TARGET_LOCATION;
    }
}
