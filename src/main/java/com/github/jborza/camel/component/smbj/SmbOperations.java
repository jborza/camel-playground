package com.github.jborza.camel.component.smbj;

import com.hierynomus.msdtyp.AccessMask;
import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.mssmb2.SMB2CreateDisposition;
import com.hierynomus.mssmb2.SMB2ShareAccess;
import com.hierynomus.smbj.SMBClient;
import com.hierynomus.smbj.auth.AuthenticationContext;
import com.hierynomus.smbj.connection.Connection;
import com.hierynomus.smbj.session.Session;
import com.hierynomus.smbj.share.DiskShare;
import com.hierynomus.smbj.share.File;
import org.apache.camel.Exchange;
import org.apache.camel.component.file.*;
import org.apache.camel.util.ExchangeHelper;
import org.apache.camel.util.IOHelper;
import org.apache.camel.util.ObjectHelper;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static com.hierynomus.mssmb2.SMB2CreateDisposition.FILE_CREATE;

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
    public boolean deleteFile(String name) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean existsFile(String name) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean renameFile(String s, String s1) throws GenericFileOperationFailedException {
        return false;
    }

    @Override
    public boolean buildDirectory(String directory, boolean b) throws GenericFileOperationFailedException {
        return false;
    }

    public static int copy(InputStream input, OutputStream output) throws IOException {
        return copy((InputStream)input, (OutputStream)output, 4096);
    }

    public static int copy(InputStream input, OutputStream output, int bufferSize) throws IOException {
        return copy(input, output, bufferSize, false);
    }

    public static int copy(InputStream input, OutputStream output, int bufferSize, boolean flushOnEachWrite) throws IOException {
        if (input instanceof ByteArrayInputStream) {
            input.mark(0);
            input.reset();
            bufferSize = input.available();
        } else {
            int avail = input.available();
            if (avail > bufferSize) {
                bufferSize = avail;
            }
        }

        if (bufferSize > 262144) {
            bufferSize = 262144;
        }

        byte[] buffer = new byte[bufferSize];
        int n = input.read(buffer);

        int total;
        for(total = 0; -1 != n; n = input.read(buffer)) {
            output.write(buffer, 0, n);
            if (flushOnEachWrite) {
                output.flush();
            }

            total += n;
        }

        if (!flushOnEachWrite) {
            output.flush();
        }

        return total;
    }

    @Override
    public boolean retrieveFile(String name, Exchange exchange) throws GenericFileOperationFailedException {
        OutputStream os = null;
        boolean result;
        try {
            os = new ByteArrayOutputStream();
            GenericFile<File> target = (GenericFile<File>)exchange.getProperty(FileComponent.FILE_EXCHANGE_FILE);
            ObjectHelper.notNull(target, "Exchange should have the " + FileComponent.FILE_EXCHANGE_FILE + " set");
            target.setBody(os);


            login();
            SmbConfiguration config = ((SmbConfiguration)endpoint.getConfiguration());

            DiskShare share = (DiskShare) session.connectShare(config.getShare());
            File f = share.openFile(name.replace('/','\\'), EnumSet.of(AccessMask.GENERIC_READ),null, SMB2ShareAccess.ALL, SMB2CreateDisposition.FILE_OPEN,null);
            InputStream is = f.getInputStream();
            copy(is,os);
            return true;
        } catch (IOException e) {
            throw new GenericFileOperationFailedException("Cannot retrieve file: " + name, e);
        } catch (Exception e) {
            throw new GenericFileOperationFailedException("Cannot retrieve file: " + name, e);
        } finally {
            IOHelper.close(os, "retrieve: " + name);
        }
    }

    @Override
    public void releaseRetreivedFileResources(Exchange exchange) throws GenericFileOperationFailedException {

    }

    @Override
    public String getCurrentDirectory() throws GenericFileOperationFailedException {
        return null;
    }

    @Override
    public void changeCurrentDirectory(String path) throws GenericFileOperationFailedException {

    }

    @Override
    public void changeToParentDirectory() throws GenericFileOperationFailedException {

    }

    @Override
    public List<File> listFiles() throws GenericFileOperationFailedException {
        return null;
    }

    private final static long FILE_ATTRIBUTE_DIRECTORY = 16L;

    public List<FileIdBothDirectoryInformation> listFilesSpecial(String path) throws GenericFileOperationFailedException {
        //TODO replace with a nicer class
        List<FileIdBothDirectoryInformation> files = new ArrayList<FileIdBothDirectoryInformation>();
        try {
            login();
            SmbConfiguration config = ((SmbConfiguration)endpoint.getConfiguration());

            DiskShare share = (DiskShare) session.connectShare(config.getShare());

            for (FileIdBothDirectoryInformation f : share.list(config.getPath())) {
                LoggerFactory.getLogger(this.getClass()).debug(f.getFileName());
                boolean isDirectory = (f.getFileAttributes() & FILE_ATTRIBUTE_DIRECTORY) == FILE_ATTRIBUTE_DIRECTORY;
                if(isDirectory)
                    continue;
                files.add(f);
            }
        } catch (Exception e) {
            throw new GenericFileOperationFailedException("Could not get files " + e.getMessage(), e);
        }
        return files;
    }

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

    @Override
    public boolean storeFile(String name, Exchange exchange) {
        String storeName = getPath(name);

        InputStream inputStream = null;
        try {
            inputStream = exchange.getIn().getMandatoryBody(InputStream.class);

            login();
            SmbConfiguration config = ((SmbConfiguration)endpoint.getConfiguration());

            DiskShare share = (DiskShare) session.connectShare(config.getShare());
            GenericFile<File> inputFile = (GenericFile<File>)exchange.getIn().getBody();
            Path path = Paths.get(config.getPath(),inputFile.getRelativeFilePath());
            File file = share.openFile(path.toString(), EnumSet.of(AccessMask.GENERIC_WRITE), null, SMB2ShareAccess.ALL, FILE_CREATE, null);

            OutputStream smbout = file.getOutputStream();
            byte[] buf = new byte[512 * 1024];
            int numRead;
            while ((numRead = inputStream.read(buf)) >= 0) {
                smbout.write(buf, 0, numRead);
            }
            smbout.close();
            //TODO set last modified date to lastModifiedDate(exchange)
            //file.setFileInformation(); ?
            return true;
        } catch (Exception e) {
            throw new GenericFileOperationFailedException("Cannot store file " + storeName, e);
        } finally {
            IOHelper.close(inputStream, "store: " + storeName);
        }
    }

    }
