package controllers;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import java.io.IOException;
import org.eclipse.egit.github.core.Repository;
import org.eclipse.egit.github.core.client.GitHubClient;
import org.eclipse.egit.github.core.service.RepositoryService;
import org.eclipse.egit.github.core.service.CommitService;
import org.eclipse.egit.github.core.service.PullRequestService;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.CloneCommand;

import java.io.File;
import org.apache.commons.io.FileUtils;

import java.util.*;

import org.springframework.test.context.ContextConfiguration;
import services.auth.AuthService;
import services.auth.User;
import services.projects.ProjectsService;
import services.projects.Project;
import services.projects.CheckResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Qualifier;

//@ContextConfiguration(classes={RESTConfiguration.class})

@Controller
public class ProjectsController {

    /*@Autowired
    @Qualifier(value="projectsService")
    private ProjectsService projectsService = new ProjectsService();
    @Autowired
    private AuthService authService;*/

    Logger log = LoggerFactory.getLogger(HelloController.class);
    private Object object;

    @RequestMapping(value = "/projects", method = RequestMethod.GET)
    public String addProject(@RequestParam(value = "project_id", required = false, defaultValue = "0") int project_id,
                        @RequestParam(value = "version_id", required = false, defaultValue = "0") int version_id,
                        Model model) {
        testSomeMethod();

        //System.out.println("project id: " + project_id + ", version_id: " + version_id + "\n");
        log.debug("In ProjectsController..");
        model.addAttribute("project_id", project_id);
        model.addAttribute("version_id", version_id);
        return "addProject";
    }

    /*@RequestMapping(value = "/runcheck",
            method = RequestMethod.GET,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> getSearchResultViaAjax(@RequestParam//@RequestBody String some) {
        System.out.println("Some: " + some);
        return new ResponseEntity<>(new CheckResult("Result", true), HttpStatus.OK);
    }*/

    @RequestMapping(value = "/addAndCheck",
            method = RequestMethod.POST,
            produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<Object> addAndCheck(@RequestParam String title,
                                                          @RequestParam String desc,
                                                          @RequestParam String owner,
                                                          @RequestParam String repo,
                                                          @RequestParam String commitOrBranch,
                                                          @RequestParam String type,
                                                          @RequestParam String username,
                                                          @RequestParam String password,
                                                          @RequestParam String attributes
    ) {
        boolean success = true, result;
        String message = "";
        String localPath = System.getProperty("user.dir") + File.separator + "projects" + File.separator + owner + "_" + repo + "_" + commitOrBranch;
        if (owner.length() == 0 || repo.length() == 0) {
            success = false;
            message = "You must specify owner and repository name!";
        }

        if (success == true) {
            result = cloneRepo(owner, repo, commitOrBranch, type, username, password, localPath);
            if (result == false) {
                message = "There was a problem with cloning the repository. If the repository is private you must fill username and password! Please, check owner, repository name, commit or branch.";
                success = false;
            }
            else {
                message = "There must be result of check by borealis.";
            }
        }
        if (success == true) {
            Project project = new Project(
                    title, desc, owner, repo, commitOrBranch, "", localPath, 0
            );
            /*User user = new User();
            user.setUsername("gg");
            user.setPassword("g");
            authService.insertUser(user);
            projectsService.insertProject(project);
            projectsService.selectProject(project.getId());*/
        }
        return new ResponseEntity<>(new CheckResult(message, success), HttpStatus.OK);
    }
/*public @ResponseBody Greeting sayHello(@RequestParam(value="name", required=false, defaultValue="Stranger") String name) {
        return new Greeting(counter.incrementAndGet(), String.format(template, name));
    }
*/
    public void testSomeMethod()
    {
        GitHubClient client = new GitHubClient();
        //client.setCredentials(username, password);

        RepositoryService service = new RepositoryService(client);
        PullRequestService service1 = new PullRequestService(client);
        //cloneRepo("MashaFomina", "DB-labs", client);

//service1.
        try {
            List<Repository> repositories = service.getRepositories();
            CommitService commitService = new CommitService(client);
            for (int i = 0; i < repositories.size(); i++) {
                Repository repo = repositories.get(i);
                //Repository repository = .getRepository("https://github.com/MashaFomina/fp_labs");
                /*System.out.println("Repository Name: " + repo.getName());
                try {
                    for (RepositoryCommit commit : commitService.getCommits(repo)) {
                        //commit.getFiles().
                        Commit temp = commit.getCommit();
                        System.out.println("Commit SHA:\n" + temp.getTree().getSha() + "\nMessage: " + temp.getMessage());
                        System.out.println("Url: " + commit.getUrl());
                        System.out.println("Url: " + temp.getUrl());
                    }
                }
                catch(IOException ie) {
                    System.out.println("Error with getCommits of " + repo.getName() + "!\n");
                }*/
                /*try {
                    // now contents service
                    ContentsService contentService = new ContentsService(client);
                    List<RepositoryContents> test = contentService.getContents(repo);
                    DownloadResource d = new DownloadResource();
                    Download dd = new Download();

                    for (RepositoryContents content : test) {
                        String fileContent = content.getContent();
                        System.out.println("Size: " + fileContent.length());
                        FileOutputStream fos = new FileOutputStream("name_t");
                        System.out.println("Name: " + content.getName());
                        System.out.println("Length:" + fileContent.getBytes().length);
                        fos.write(fileContent.getBytes());
                        fos.close();
                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }*/
            }
        }
        catch(IOException ie) {
            System.out.println("Error with getRepositories!\n");
        }
    }
    private static boolean isExists(String link, String commitOrBranch, int type)
    {
        boolean result = false;
        return result;
    }

    private static boolean cloneRepo(String owner, String repo, String commitOrBranch, String type, String username, String password, String localPath)
    {
        boolean result = true;
        GitHubClient client = new GitHubClient();
        if (username.length() > 0 && password.length() > 0) {
            client.setCredentials(username, password);
        }
        Git git = null;
        File dir = new File(localPath);
        try {
            RepositoryService rs = new RepositoryService(client);
            Repository r = rs.getRepository(owner, repo);

            String cloneURL = r.getCloneUrl();
            /*
            System.out.println(r.getSshUrl()); // git@github.com:MashaFomina/DB-labs.git, problem with UnknownHostKey: github.com, ssh public key
            // Two variants works
            System.out.println(r.getCloneUrl()); //https://github.com/MashaFomina/DB-labs.git
            System.out.println(r.getGitUrl()); // git://github.com/MashaFomina/DB-labs.git
            */

            // prepare a new folder for the cloned repository
            if (dir.isDirectory() != false) {
                FileUtils.deleteDirectory(dir);
            }
            dir.mkdirs();

            // Clone the repository
            CloneCommand command = Git.cloneRepository();
            command = Git.cloneRepository()
                    .setURI(cloneURL)
                    .setDirectory(dir);
            if (commitOrBranch.length() > 0 && type.equals("1")) {
                command.setBranch(commitOrBranch);
            }
            git = command.call();

            // Checkout on commit
            if (commitOrBranch.length() > 0 && type.equals("0")) {
                git.checkout().setName(commitOrBranch).call();
            }
            System.out.println("Branch: " + git.getRepository().getBranch());
        } catch (IOException | GitAPIException ex) {
            System.out.println("There was a problem with cloning the repository: " + ex.getMessage());
            result = false;
        } finally {
            if (git != null) {
                git.close();
            }

            if (result == false) {
                if (dir.isDirectory() != false) {
                    try {
                        FileUtils.deleteDirectory(dir);
                    }
                    catch (IOException ex) {}
                }
            }
        }
        /**
         * Get tree with given SHA-1
         *
         * @param repository
         * @param sha
         * @param recursive
         * @return tree
         * @throws IOException
         */
        /*public Tree getTree(IRepositoryIdProvider repository, String sha,
        boolean recursive) throws IOException {
            final String id = getId(repository);
            if (sha == null)
                throw new IllegalArgumentException("SHA-1 cannot be null"); //$NON-NLS-1$
            if (sha.length() == 0)
                throw new IllegalArgumentException("SHA-1 cannot be empty"); //$NON-NLS-1$

            StringBuilder uri = new StringBuilder();
            uri.append(SEGMENT_REPOS);
            uri.append('/').append(id);
            uri.append(SEGMENT_GIT);
            uri.append(SEGMENT_TREES);
            uri.append('/').append(sha);
            GitHubRequest request = createRequest();
            request.setType(Tree.class);
            request.setUri(uri);
            if (recursive)
                request.setParams(Collections.singletonMap("recursive", "1")); //$NON-NLS-1$ //$NON-NLS-2$
            return (Tree) client.get(request).getBody();
        }*/
        return result;
    }
}