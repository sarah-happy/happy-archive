package org.yi.happy.archive;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.Map;

import org.yi.happy.archive.commandLine.Env;
import org.yi.happy.archive.file_system.RealFileSystem;

public class MyInjector {

    public static MainCommand injectFileStoreTagPutMain(ApplicationScope scope) {
        return new FileStoreTagPutMain(injectBlockStore(scope),
                injectFileSystem(scope), injectOutput(scope), injectEnv(scope));
    }

    public static PrintStream injectOutput(ApplicationScope scope) {
        return System.out;
    }

    public static RealFileSystem injectFileSystem(ApplicationScope scope) {
        return new RealFileSystem();
    }

    public static MainCommand injectFileStoreTagGetMain(ApplicationScope scope) {
        return new FileStoreTagGetMain(injectBlockStore(scope),
                injectFileSystem(scope), injectWaitHandler(scope),
                injectInput(scope), injectError(scope), injectEnv(scope));
    }

    public static PrintStream injectError(ApplicationScope scope) {
        return System.err;
    }

    public static InputStream injectInput(ApplicationScope scope) {
        return System.in;
    }

    public static WaitHandlerProgressiveDelay injectWaitHandler(
            ApplicationScope scope) {
        return new WaitHandlerProgressiveDelay();
    }

    public static MainCommand injectShowEnvMain(ApplicationScope scope) {
        return new ShowEnvMain(injectEnv(scope));
    }

    public static MainCommand injectLocalCandidateListMain(
            ApplicationScope scope) {
        return new LocalCandidateListMain(injectBlockStore(scope),
                injectEnv(scope));
    }

    public static MainCommand injectStoreRemoveMain(ApplicationScope scope) {
        return new StoreRemoveMain(injectBlockStore(scope),
                injectFileSystem(scope), injectError(scope), injectEnv(scope));
    }

    public static MainCommand injectFileStoreListMain(ApplicationScope scope) {
        return new FileStoreListMain(injectBlockStore(scope),
                injectFileSystem(scope), injectOutput(scope),
                injectError(scope));
    }

    public static MainCommand injectIndexSearchMain(ApplicationScope scope) {
        return new IndexSearchMain(injectFileSystem(scope),
                injectOutput(scope), injectEnv(scope));
    }

    public static MainCommand injectIndexVolumeMain(ApplicationScope scope) {
        return new IndexVolumeMain(injectFileSystem(scope),
                injectOutput(scope), injectError(scope), injectEnv(scope));
    }

    public static MainCommand injectBuildImageMain(ApplicationScope scope) {
        return new BuildImageMain(injectBlockStore(scope),
                injectFileSystem(scope), injectOutput(scope),
                injectError(scope), injectEnv(scope));
    }

    public static Env injectEnv(ApplicationScope scope) {
        return scope.getEnv();
    }

    public static MainCommand injectVolumeGetMain(ApplicationScope scope) {
        return new VolumeGetMain(injectBlockStore(scope),
                injectFileSystem(scope), injectInput(scope),
                injectError(scope), injectEnv(scope));
    }

    public static MainCommand injectVerifyMain(ApplicationScope scope) {
        return new VerifyMain(injectFileSystem(scope), injectOutput(scope),
                injectEnv(scope));
    }

    public static MainCommand injectEncodeContentMain(ApplicationScope scope) {
        return new EncodeContentMain(injectFileSystem(scope),
                injectOutput(scope), injectEnv(scope));
    }

    public static MainCommand injectDecodeBlockMain(ApplicationScope scope) {
        return new DecodeBlockMain(injectFileSystem(scope),
                injectOutput(scope), injectEnv(scope));
    }

    public static MainCommand injectFileStoreTagAddMain(ApplicationScope scope) {
        return new FileStoreTagAddMain(injectBlockStore(scope),
                injectFileSystem(scope));
    }

    public static MainCommand injectFileStoreStreamPutMain(
            ApplicationScope scope) {
        return new FileStoreStreamPutMain(injectBlockStore(scope),
                injectFileSystem(scope), injectInput(scope),
                injectOutput(scope));
    }

    public static MainCommand injectFileStoreStreamGetMain(
            ApplicationScope scope) {
        return new FileStoreStreamGetMain(injectBlockStore(scope),
                injectFileSystem(scope), injectOutput(scope),
                injectWaitHandler(scope), injectEnv(scope));
    }

    public static MainCommand injectFileStoreBlockPutMain(ApplicationScope scope) {
        return new FileStoreBlockPutMain(injectBlockStore(scope),
                injectFileSystem(scope), injectEnv(scope));
    }

    public static MainCommand injectMakeIndexDatabaseMain(ApplicationScope scope) {
        return new MakeIndexDatabaseMain();
    }

    public static MainCommand injectFileStoreFileGetMain(ApplicationScope scope) {
        return new FileStoreFileGetMain(injectBlockStore(scope),
                injectFileSystem(scope), injectWaitHandler(scope),
                injectEnv(scope));
    }

    public static BlockStore injectBlockStore(ApplicationScope scope) {
        return new FileBlockStore(injectFileSystem(scope), scope.getStore());
    }

    public static MainCommand injectHelpMain(ApplicationScope scope) {
        return new HelpMain(injectOutput(scope), injectCommands(scope));
    }

    public static Map<String, Class<? extends MainCommand>> injectCommands(
            ApplicationScope scope) {
        return scope.getCommands();
    }
}
