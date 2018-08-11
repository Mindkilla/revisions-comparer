package repo;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.ResetCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;
//https://github.com/centic9/jgit-cookbook
public class GitApi {
    public static void repoOperations() throws IOException {
        // Открыть существующий репозиторий
        Repository existingRepo = new FileRepositoryBuilder()
                .setGitDir(new File("my_repo/.git"))
                .build();
        Git git = new Git(existingRepo);
        try {
            git.reset().setMode(ResetCommand.ResetType.HARD).setRef("sdfsdfsdf").call();
        } catch (GitAPIException e) {
            e.printStackTrace();
        }
    }

}
