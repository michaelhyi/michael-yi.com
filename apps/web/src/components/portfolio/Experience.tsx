"use client";

import type ExperienceDto from "types/experience";
import { experience } from "@/data/experience";
import Card from "../Card";
import Section from "../Section";

const Experience = () => {
  return (
    <Section title="Experience">
      {experience.map((v: ExperienceDto) => (
        <Card date={v.date} desc={v.description} key={v.name} name={v.name} />
      ))}
    </Section>
  );
};

export default Experience;
