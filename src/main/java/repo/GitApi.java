package repo;

import org.apache.log4j.Logger;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;

//https://github.com/centic9/jgit-cookbook
public class GitApi {
    public static final Logger logger = Logger.getLogger(GitApi.class);

    public static void checkoutHard(String revisionSHA, String repoDestination) throws IOException {
        // Открыть существующий репозиторий
        Repository existingRepo = new FileRepositoryBuilder()
                .setGitDir(new File(repoDestination))
                .build();
        Git git = new Git(existingRepo);
        try {
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef(revisionSHA).call();
            logger.info("Finishing checkout - " + repoDestination);
        } catch (GitAPIException e) {
            logger.error(e.getMessage());
        }
    }

    public static void pull(String remoteBranchName, String repoDestination) throws IOException {
        // Открыть существующий репозиторий
        Repository existingRepo = new FileRepositoryBuilder()
                .setGitDir(new File(repoDestination))
                .build();
        Git git = new Git(existingRepo);
        try {
            git.pull()
                    .setRemoteBranchName(remoteBranchName)
                    .setCredentialsProvider(new UsernamePasswordCredentialsProvider("login", "pass"))
                    .call();
            logger.info("Finishing pull - " + repoDestination);
        } catch (GitAPIException e) {
            logger.error(e.getMessage());
        }
    }

    public static void getRevision(String remoteBranchName, String repoDestination, String revisionSHA) throws IOException {
        // Сначала обновляем репозиторий
        pull(remoteBranchName, repoDestination);
        //Потом получаем конкретную ревизию для сравнения
        checkoutHard(revisionSHA, repoDestination);
    }

    public static void prepareRepos(String branchName, String revisionOld, String revisionNew) throws IOException {
        //Обновляем репозиторий со старой ревизией
        getRevision(branchName, "D:\\git_tests\\old\\box-legacy\\.git", revisionOld);
        //Обновляем репозиторий с новой ревизией
        getRevision(branchName, "D:\\git_tests\\new\\box-legacy\\.git", revisionNew);
    }

}
