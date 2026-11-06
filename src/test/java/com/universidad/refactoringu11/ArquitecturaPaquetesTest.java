package com.universidad.refactoringu11;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import org.junit.jupiter.api.Test;
import com.tngtech.archunit.core.importer.ImportOption;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

class ArquitecturaPaquetesTest {

    @Test
    void losPaquetesNoDebenTenerDependenciasCiclicas() {
        JavaClasses clases = new ClassFileImporter()
                .importPackages("com.universidad.refactoringu11");

        slices().matching("com.universidad.refactoringu11.(*)..")
                .should().beFreeOfCycles()
                .check(clases);
    }

    @Test
    void inventarioNoDebeDependerDeReportes() {
        JavaClasses clasesProduccion = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.universidad.refactoringu11");

        noClasses().that().resideInAPackage("..inventario..")
            .should().dependOnClassesThat().resideInAPackage("..reportes..")
            .check(clasesProduccion);
    }

}