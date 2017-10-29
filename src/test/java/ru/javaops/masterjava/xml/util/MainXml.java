package ru.javaops.masterjava.xml.util;

import com.google.common.io.Resources;
import ru.javaops.masterjava.xml.schema.*;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Created by MSI on 29.10.2017.
 */
public class MainXml {

    private static final JaxbParser JAXB_PARSER = new JaxbParser(ObjectFactory.class);

    static {
        JAXB_PARSER.setSchema(Schemas.ofClasspath("payload.xsd"));
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.out.println("Set project name");
            return;
        }
        Payload payload = JAXB_PARSER.unmarshal(
                Resources.getResource("payload.xml").openStream());
        Optional<Project> projectOpt = payload
                .getProjects()
                .getProject()
                .stream()
                .filter((project -> project.getTitle().equals(args[0])))
                .findAny();

        if (projectOpt.isPresent()) {
            Project project = projectOpt.get();
            List<Group> groups = payload
                    .getGroups()
                    .getGroup()
                    .stream()
                    .filter(group -> group.getProject().equals(project))
                    .collect(Collectors.toList());

            payload.getUsers()
                    .getUser()
                    .stream()
                    .filter(user -> user.getGroups()
                            .getGroup()
                            .stream()
                            .map(obj -> obj.getValue())
                            .anyMatch(e -> groups.contains(e)))
                    .sorted(Comparator.comparing(User::getFullName))
                    .forEach(user -> System.out.println(user.getFullName() + "/" + user.getEmail()));
        }
    }
}
