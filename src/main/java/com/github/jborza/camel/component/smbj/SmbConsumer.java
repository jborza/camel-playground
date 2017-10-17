package com.github.jborza.camel.component.smbj;

import com.hierynomus.msfscc.fileinformation.FileIdBothDirectoryInformation;
import com.hierynomus.smbj.share.File;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.file.GenericFile;
import org.apache.camel.component.file.GenericFileConsumer;
import org.apache.camel.component.file.GenericFileEndpoint;
import org.apache.camel.component.file.GenericFileOperations;

import java.util.List;

import static java.util.stream.Collectors.toList;

public class SmbConsumer extends GenericFileConsumer<File> {

    private String endpointPath;
    private String currentRelativePath = "";

    public SmbConsumer(GenericFileEndpoint<File> endpoint, Processor processor, GenericFileOperations<File> operations) {
        super(endpoint, processor, operations);
        SmbConfiguration config = (SmbConfiguration) endpoint.getConfiguration();
        this.endpointPath = config.getPath();
    }

    private SmbOperations getOperations(){
        return (SmbOperations) operations;
    }

    @Override
    protected boolean pollDirectory(String fileName, List<GenericFile<File>> fileList, int depth) {

        if (log.isTraceEnabled()) {
            log.trace("pollDirectory() running. My delay is [" + this.getDelay() + "] and my strategy is [" + this.getPollStrategy().getClass().toString() + "]");
            log.trace("pollDirectory() fileName[" + fileName + "]");
        }

        //TODO doing 1 level just now
        List<FileIdBothDirectoryInformation> smbFiles = getOperations().listFilesSpecial(fileName);
        List<GenericFile<File>> genericFiles = smbFiles.stream()
                .map(f -> asGenericFile(f))
                .collect(toList());
        for (GenericFile<File> gf:genericFiles) {
            fileList.add(gf);
        }
        return true;

//        List<DiskEntry> smbFiles;
//        boolean currentFileIsDir = false;
//        smbFiles = operations.listFiles(fileName);
//        for (DiskEntry smbFile : smbFiles) {
//            if (!canPollMoreFiles(fileList)) {
//                return false;
//            }
//            try {
//                if (smbFile.isDirectory()) {
//                    currentFileIsDir = true;
//                } else {
//                    currentFileIsDir = false;
//                }
//            } catch (SmbException e1) {
//                throw ObjectHelper.wrapRuntimeCamelException(e1);
//            }
//            if (currentFileIsDir) {
//                if (endpoint.isRecursive()) {
//                    currentRelativePath = smbFile.getName().split("/")[0] + "/";
//                    int nextDepth = depth++;
//                    pollDirectory(fileName + "/" + smbFile.getName(), fileList, nextDepth);
//                } else {
//                    currentRelativePath = "";
//                }
//            } else {
//                try {
//                    GenericFile<DiskEntry> genericFile = asGenericFile(fileName, smbFile);
//                    if (isValidFile(genericFile, false, smbFiles)) {
//                        fileList.add(asGenericFile(fileName, smbFile));
//                    }
//                } catch (IOException e) {
//                    throw ObjectHelper.wrapRuntimeCamelException(e);
//                }
//            }
//        }
//        return true;
    }

    @Override
    protected void updateFileHeaders(GenericFile<File> genericFile, Message message) {
        // TODO
    }

    private GenericFile<File> asGenericFile(FileIdBothDirectoryInformation info){
        GenericFile<File> f = new GenericFile<>();
        f.setRelativeFilePath(info.getFileName());
        f.setAbsolute(true);
        f.setEndpointPath(endpointPath);
        f.setAbsoluteFilePath(endpointPath+"/"+info.getFileName());
//        f.setAbsoluteFilePath(path);
        return f;
    }

    // TODO: this needs some checking!
//    private GenericFile<SmbFile> asGenericFile(String path, SmbFile file) throws IOException {
//        SmbGenericFile<SmbFile> answer = new SmbGenericFile<SmbFile>();
//        answer.setAbsoluteFilePath(path + answer.getFileSeparator() + file.getName());
//        answer.setAbsolute(true);
//        answer.setEndpointPath(endpointPath);
//        answer.setFileNameOnly(file.getName());
//        answer.setFileLength(file.getContentLength());
//        answer.setFile(file);
//        answer.setLastModified(file.getLastModified());
//        answer.setFileName(currentRelativePath + file.getName());
//        answer.setRelativeFilePath(file.getName());
//
//        if (log.isTraceEnabled()) {
//            log.trace("asGenericFile():");
//            log.trace("absoluteFilePath[" + answer.getAbsoluteFilePath() + "] endpointpath[" + answer.getEndpointPath() + "] filenameonly[" + answer.getFileNameOnly()
//                    + "] filename[" + answer.getFileName() + "] relativepath[" + answer.getRelativeFilePath() + "]");
//        }
//        return answer;
//    }

    @Override
    protected boolean isMatched(GenericFile<File> file, String doneFileName, List<File> files) {
//        String onlyName = FileUtil.stripPath(doneFileName);
//
//        for (DiskEntry f : files) {
//            if (f.getFileInformation().getNameInformation().equals(onlyName)) {
//                return true;
//            }
//        }
//
//        log.trace("Done file: {} does not exist", doneFileName);
        return false;
    }

    @Override
    protected boolean isRetrieveFile() {
        return ((SmbEndpoint)getEndpoint()).isDownload();
    }

}
