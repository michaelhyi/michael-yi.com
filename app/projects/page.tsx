import Container from "@/app/components/Container";
import { projects } from "@/app/data/projects";

const Projects = () => {
  return (
    <Container>
      <div className="mt-36 text-3xl font-bold">Projects</div>
      <br />
      <div className="flex flex-col gap-4">
        {projects.map((v) => (
          <a
            rel="noopener noreferrer"
            target="_blank"
            className="p-4 shadow-md cursor-pointer duration-300 hover:shadow-lg hover:bg-neutral-50"
            key={v.name}
            href={v.href}
          >
            <div className="font-bold text-lg">{v.name}</div>
            <div className="text-sm">{v.desc}</div>
            <div className="text-xs mt-2 opacity-75">{v.tech}</div>
          </a>
        ))}
      </div>
    </Container>
  );
};

export default Projects;