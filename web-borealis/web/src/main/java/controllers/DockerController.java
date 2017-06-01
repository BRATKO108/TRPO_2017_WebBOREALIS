package controllers;

import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.LogStream;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockerController {
    public static String makeCheck(String pathout, String cName) throws DockerException, InterruptedException, DockerCertificateException, IOException {

        // pathout - Путь до программы
        // cName - Имя исполняемого файла программы
        final DockerClient docker = DefaultDockerClient.fromEnv().build();
        System.out.println( docker.info().kernelVersion() );
        String name = "vorpal/borealis-standalone";
        System.out.println( docker.listImages(DockerClient.ListImagesParam.byName("vorpal/borealis-standalone")));

        //String dockid = "70cfbbcb5048"; //for check image
        String pathIn = "/home/borealis/borealis/build";

        //final ImageInfo infoim = docker.inspectImage(dockid);
        //System.out.println( infoim.toString() );
        //
        //docker.startContainer(dockid);

        // Bind container ports to host ports
        final String[] ports = {"80", "24"};
        final Map<String, List<PortBinding>> portBindings = new HashMap<>();
        for (String port : ports) {
            List<PortBinding> hostPorts = new ArrayList<>();
            hostPorts.add(PortBinding.of("0.0.0.0", port));
            portBindings.put(port, hostPorts);
        }

        List<PortBinding> randomPort = new ArrayList<>();
        randomPort.add(PortBinding.randomPort("0.0.0.0"));
        portBindings.put("443", randomPort);

        final HostConfig hostConfig = HostConfig.builder().portBindings(portBindings).build();
        final ContainerConfig containerConfig = ContainerConfig.builder()
                .hostConfig(hostConfig)
                .image(name).exposedPorts(ports)
                .cmd("sh", "-c", "while :; do sleep 1; done")
                .build();

        final ContainerCreation creation = docker.createContainer(containerConfig);
        final String id = creation.id();


        final ContainerInfo info = docker.inspectContainer(id);

        // Start container
        docker.startContainer(id);




        Path path = Paths.get(pathout);
        docker.copyToContainer(path, id, pathIn); // add project to docker image

        final String[] command = {"bash", "-c", "../wrapper /home/borealis/borealis/build/" + cName};
        final ExecCreation execCreation = docker.execCreate(
                id, command, DockerClient.ExecCreateParam.attachStdout(),
                DockerClient.ExecCreateParam.attachStderr());
        final LogStream output = docker.execStart(execCreation.id());
        final String execOutput = output.readFully();
        System.out.println(execOutput);

        // Kill container
        docker.killContainer(id);

        // Remove container
        docker.removeContainer(id);

        // Close the docker client
        docker.close();


        //docker.stopContainer(dockid, 10);
        return execOutput;
    }
}