import FiArrowUpRight from "../assets/icons/FiArrowUpRight";
import BackButton from "../components/BackButton";
import Container from "../components/Container";
import Hoverable from "../components/Hoverable";
import Project from "../components/Project";
import HACKATHONS from "../constants/hackathons";
import PROJECTS from "../constants/projects";

export default function Home() {
    return (
        <Container>
            <BackButton href="/" text="Home" />
            <section className="mt-10 flex flex-col gap-8">
                <h1 className="text-3xl font-bold">Projects</h1>
                {PROJECTS.map((project) => (
                    <Project key={project.name} project={project} />
                ))}
            </section>
            <section className="mt-16 flex flex-col gap-2">
                <h1 className="text-3xl font-bold mb-6">Hackathons</h1>
                {HACKATHONS.map((hackathon) => (
                    <Hoverable className="text-left" key={hackathon.name}>
                        <a
                            className="text-sm font-medium"
                            href={hackathon.href}
                        >
                            {hackathon.name}
                            <span className="inline-block">
                                <FiArrowUpRight />
                            </span>
                        </a>
                    </Hoverable>
                ))}
            </section>
        </Container>
    );
}
