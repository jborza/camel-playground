package com.github.jborza.camel.component.smbj;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.GenericFileEndpoint;
import org.apache.camel.component.file.GenericFileOperationFailedException;
import org.apache.camel.component.file.GenericFileOperations;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class SmbOperations implements GenericFileOperations<File> {
    private final SMBClient client;
    private Session session;
    private GenericFileEndpoint<File> endpoint;
    
    public SmbOperations(SMBClient client){this.client = client;}

    @Override
    public void setEndpoint(GenericFileEndpoint<File> genericFileEndpoint) {
        this.endpoint = genericFileEndpoint;
    }

    @Override
    public boolean deleteFile(String s) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean existsFile(String s) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean renameFile(String s, String s1) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean buildDirectory(String s, boolean b) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean retrieveFile(String s, Exchange exchange) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public void releaseRetreivedFileResources(Exchange exchange) throws GenericFileOperationFailedException {

    }

    @Override
    public boolean storeFile(String s, Exchange exchange) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public String getCurrentDirectory() throws GenericFileOperationFailedException {
        return null;
    }

    @Override
    public void changeCurrentDirectory(String s) throws GenericFileOperationFailedException {

    }

    @Override
    public void changeToParentDirectory() throws GenericFileOperationFailedException {

    }

    @Override
    public List<File> listFiles() throws GenericFileOperationFailedException {
        return null;
    }

    private final static long FILE_ATTRIBUTE_DIRECTORY = 16L;

    @Override
    public List<File> listFiles(String path) throws GenericFileOperationFailedException {
        String listPath = getDirPath(path);
        List<File> files = new ArrayList<File>();
        try {
            login();
            SmbConfiguration config = ((SmbConfiguration)endpoint.getConfiguration());

            DiskShare share = (DiskShare) session.connectShare(config.getShare());

            for (FileIdBothDirectoryInformation f : share.list(config.getPath())) {
                LoggerFactory.getLogger(this.getClass()).debug(f.getFileName());
                boolean isDirectory = false;
                if((f.getFileAttributes() & FILE_ATTRIBUTE_DIRECTORY) == FILE_ATTRIBUTE_DIRECTORY)
                    isDirectory = true;
                System.out.println((isDirectory?"DIR ":"FILE")+" - "+ f.getFileName());
                //files.add((File)f);
                //TODO list files
                //File file= new File(f.getFileId(),f.getFileName(),share);

            }
        } catch (Exception e) {
            throw new GenericFileOperationFailedException("Could not get files " + e.getMessage(), e);
        }
        return files;
    }

    private String getPath(String pathEnd) {
        String path = ((SmbConfiguration)endpoint.getConfiguration()).getSmbHostPath() + pathEnd;
        return path.replace('\\', '/');
    }

    private String getDirPath(String pathEnd) {
        String path = ((SmbConfiguration)endpoint.getConfiguration()).getSmbHostPath() + pathEnd;
        if (!path.endsWith("/")) {
            path = path + "/";
        }
        return path.replace('\\', '/');
    }

    public void login() {
        SmbConfiguration config = ((SmbConfiguration)endpoint.getConfiguration());

        String domain = config.getDomain();
        String username = config.getUsername();
        String password = config.getPassword();

        try {
            Connection connection = client.connect(config.getHost());
            session = connection.authenticate(new AuthenticationContext(username, password.toCharArray(), domain));
        }
        catch(IOException e){
            //TODO what now?
        }
    }

}
