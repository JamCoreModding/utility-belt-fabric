import {
  Input,
  prompt,
} from "https://deno.land/x/cliffy@v0.23.0/prompt/mod.ts";

const options = await prompt([
  {
    name: "mod_name",
    message: "Enter mod name:",
    type: Input,
  },
  {
    name: "mod_id",
    message: "Enter mod ID:",
    type: Input,
  },
  {
    name: "main_class_name",
    message: "Enter main class name:",
    type: Input,
  },
  {
    name: "maven_group",
    message: "Enter maven group:",
    type: Input,
    default: "io.github.jamalam360",
  },
  {
    name: "description",
    message: "Enter description:",
    type: Input,
  },
  {
    name: "author",
    message: "Enter author:",
    type: Input,
    default: "Jamalam",
  },
  {
    name: "github_user",
    message: "Enter github user/organisation:",
    type: Input,
    default: "JamCoreModding",
  },
  {
    name: "github_repo",
    message: "Enter github repo:",
    type: Input,
  }
]);

const mainPackage = `${options.maven_group}/${
  options.mod_id!.split("_").join("/").split("-").join("/")
}`.replaceAll(".", "/");
const mainClass =
  `${Deno.cwd()}/src/main/java/${mainPackage.replaceAll(".", "/")}/${options.main_class_name}.java`;

await transformMainPackage();
await transformMainClass();
await transformAssetsDirectory();
await transformFabricModJson();
await transformMixinsJson();
await transformGradleProperties();
await transformReadme();
await transformLicense();
await transformChangelogs();

async function transformMainPackage() {
  await Deno.mkdir(`${Deno.cwd()}/src/main/java/${mainPackage.replaceAll(".", "/")}`, { recursive: true });

  await Deno.rename(
    `${Deno.cwd()}/src/main/java/io/github/jamalam360/templatemod`,
    `${Deno.cwd()}/src/main/java/${mainPackage.replaceAll(".", "/")}`,
  );
}

async function transformMainClass() {
  await Deno.rename(
    `${Deno.cwd()}/src/main/java/${mainPackage}/TemplateModInit.java`,
    mainClass,
  );

  const content = await Deno.readTextFile(mainClass);

  await Deno.writeTextFile(
    mainClass,
    content.replaceAll("TemplateModInit", options.main_class_name!)
      .replaceAll("templatemod", options.mod_id!)
      .replaceAll("Template Mod", options.mod_name!)
      .replaceAll("io.github.jamalam360.templatemod", mainPackage),
  );
}

async function transformAssetsDirectory() {
  await Deno.rename(
    `${Deno.cwd()}/src/main/resources/assets/templatemod`,
    `${Deno.cwd()}/src/main/resources/assets/${options.mod_id}`,
  );
}

async function transformFabricModJson() {
  const fmj = `./src/main/resources/fabric.mod.json`;
  const fmjContent = await Deno.readTextFile(fmj);
  await Deno.writeTextFile(
    fmj,
    fmjContent
      .replaceAll(
        "io.github.jamalam360.templatemod.TemplateModInit",
        mainClass.substring(`${Deno.cwd()}/src/main/java/`.length, ".java".length).replaceAll("/", "."),
      )
      .replaceAll("templatemod", options.mod_id!)
      .replaceAll("Template Mod", options.mod_name!)
      .replaceAll("A Fabric mod template", options.description!)
      .replaceAll("Jamalam", options.author!)
      .replaceAll("JamCoreModding", options.github_user!)
      .replaceAll("FabricTemplateMod", options.github_repo!),
  );
}

async function transformMixinsJson() {
    const mixins = `${Deno.cwd()}/src/main/resources/${options.mod_id}.mixins.json`;
    await Deno.rename(`${Deno.cwd()}/src/main/resources/templatemod.mixins.json`, mixins);
    const mixinsContent = await Deno.readTextFile(mixins);
    await Deno.writeTextFile(
        mixins,
        mixinsContent.replaceAll("io.github.jamalam360.templatemod", mainPackage.replaceAll("/", "."))
    );
}

async function transformGradleProperties() {
  const gradleProperties = `${Deno.cwd()}/gradle.properties`;
  const gradlePropertiesContent = await Deno.readTextFile(gradleProperties);
  await Deno.writeTextFile(
    gradleProperties,
    gradlePropertiesContent
    .replaceAll("FabricTemplateMod", options.github_repo!)
    .replaceAll("JamCoreModding", options.github_user!)
    .replaceAll("template-mod", options.mod_id!.replaceAll("_", "-"))
  );
}

async function transformReadme() {
    await Deno.writeTextFile(
        "./README.md",
        `
# ${options.mod_name}

${options.description}
        `
    );
}

async function transformLicense() {
    await Deno.writeTextFile(
        "./LICENSE",
        await (await Deno.readTextFile("./LICENSE")).replaceAll("Jamalam", options.author!)
    );
}

async function transformChangelogs() {
  await Deno.writeTextFile(
      "./CHANGELOG_TEMPLATE.md",
      (await Deno.readTextFile("./CHANGELOG_TEMPLATE.md"))
          .replaceAll("FabricTemplateMod", options.github_repo!)
          .replaceAll("JamCoreModding", options.github_user!)
  );

  await Deno.writeTextFile(
      "./CHANGELOG.md",
      await Deno.readTextFile("./CHANGELOG_TEMPLATE.md")
  )
}
