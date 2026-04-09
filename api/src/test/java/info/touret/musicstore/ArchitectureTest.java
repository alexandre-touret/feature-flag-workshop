package info.touret.musicstore;


import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.library.Architectures.onionArchitecture;

@AnalyzeClasses(packages = {ArchitectureTest.APPLICATION_ROOT_PACKAGE,
        ArchitectureTest.DOMAIN_ROOT_PACKAGE,
        ArchitectureTest.INFRASTRUCTURE_ROOT_PACKAGE},
        importOptions = ImportOption.DoNotIncludeTests.class)
public class ArchitectureTest {

    public static final String DOMAIN_ROOT_PACKAGE = "info.touret.musicstore.domain";
    public static final String APPLICATION_ROOT_PACKAGE = "info.touret.musicstore.application";
    public static final String INFRASTRUCTURE_ROOT_PACKAGE = "info.touret.musicstore.infrastructure";

    @ArchTest
    public static final ArchRule should_return_hexagonal_architecture_is_respected =
            onionArchitecture().domainModels("..domain.model..")
                    .domainServices("..domain.(service|port|exception)..")
                    .applicationServices("..application..")
                    .adapter("database", "..database..")
                    .adapter("featureflag", "..featureflag..")
                    .ensureAllClassesAreContainedInArchitecture();

    @ArchTest
    public static final ArchRule should_return_the_domain_doesnt_depend_on_jakarta = noClasses().that().resideInAPackage(ArchitectureTest.DOMAIN_ROOT_PACKAGE + "..").should().accessClassesThat().resideInAPackage("jakarta..");

    @ArchTest
    public static final ArchRule should_return_the_domain_has_no_dependency = classes().that().resideInAPackage(ArchitectureTest.DOMAIN_ROOT_PACKAGE + "..").should().onlyAccessClassesThat().resideInAnyPackage(ArchitectureTest.DOMAIN_ROOT_PACKAGE + "..", "java..", "org.slf4j..");

    @ArchTest
    public static final ArchRule should_return_the_application_layer_doesnt_depends_on_the_infrastructure = noClasses().that().resideInAPackage(ArchitectureTest.APPLICATION_ROOT_PACKAGE + "..").should().accessClassesThat().resideInAPackage(ArchitectureTest.INFRASTRUCTURE_ROOT_PACKAGE + "..");

    @ArchTest
    public static final ArchRule should_return_the_infrastructure_layer_doesnt_depends_on_the_application = noClasses().that().resideInAPackage(ArchitectureTest.INFRASTRUCTURE_ROOT_PACKAGE + "..").should().accessClassesThat().resideInAPackage(ArchitectureTest.APPLICATION_ROOT_PACKAGE + "..");
}
