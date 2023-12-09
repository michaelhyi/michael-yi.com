import {
  readAllPosts,
  readPost,
  readUserByEmail,
} from "@personal-website/services";
import type { Post, User } from "@personal-website/types";
import { notFound, redirect } from "next/navigation";
import BlogClient from "./blog-client";
import CreateEditBlogClient from "./create-edit-blog-client";

export default async function Blog({
  searchParams,
}: {
  searchParams: Record<string, string | undefined>;
}) {
  const user: User | null = await readUserByEmail();
  let title: string | null = null;
  let content: string | null = null;

  if (!user) redirect("/");

  if (Object.keys(searchParams).length === 0) {
    const data: Post[] = await readAllPosts();

    return <BlogClient user={user} data={data} />;
  } else if (searchParams.dialog === "edit" && searchParams.id) {
    const post = await readPost(parseInt(searchParams.id));

    if (!post) notFound();

    title = post.title;
    content = `<h1>${post.title}</h1>${post.content}`;
  } else if (searchParams.dialog !== "create") {
    notFound();
  }

  return (
    <CreateEditBlogClient
      id={searchParams.id ? parseInt(searchParams.id) : null}
      title={title}
      content={
        // eslint-disable-next-line @typescript-eslint/no-unsafe-assignment -- JSON.parse is safe
        JSON.parse(JSON.stringify(content))
      }
    />
  );
}
