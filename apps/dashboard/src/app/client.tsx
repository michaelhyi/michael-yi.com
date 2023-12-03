"use client";

import Image from "next/image";
import Link from "next/link";
import { useCallback, useState } from "react";
import { useForm, type FieldValues, type SubmitHandler } from "react-hook-form";
import { login } from "services";

const LoginClient = () => {
  const [error, setError] = useState<string | null>(null);
  const [submitting, setSubmitting] = useState<boolean>(false);

  const { register, handleSubmit } = useForm({
    defaultValues: {
      email: "",
      password: "",
    },
  });

  const handleLogin: SubmitHandler<FieldValues> = useCallback(async (data) => {
    await login(data, setSubmitting, setError);
  }, []);

  return (
    <div className="bg-neutral-800 text-white min-h-screen">
      <div
        className="absolute
   left-1/2
   top-1/2
   -translate-x-1/2
   -translate-y-1/2
   transform
   flex
   flex-col
   items-center
   text-center"
      >
        <Image
          alt="michael"
          className="rounded-full"
          height={100}
          src="/michael.png"
          width={100}
        />
        <div className="mt-4 text-2xl font-light">Michael Yi</div>
        <div className="mt-1 text-xs font-light text-neutral-400">
          Personal Website Dashboard
        </div>
        <div className="flex flex-col gap-4 text-left mt-6 text-xs text-neutral-300">
          <div>Email</div>
          <input
            className="bg-neutral-800 border-[1px] border-neutral-300 rounded-md w-64 h-10 px-2"
            id="email"
            disabled={submitting}
            {...register("email")}
          />
          <div>Password</div>
          <input
            className="bg-neutral-800 border-[1px] border-neutral-300 rounded-md w-64 h-10 px-2"
            id="password"
            disabled={submitting}
            {...register("password")}
            type="password"
          />
          {error !== null && (
            <div className="text-xs text-red-300">{error}</div>
          )}
          <button
            onClick={(e) => handleSubmit(handleLogin)(e)}
            className="mt-2 bg-neutral-400 text-white h-10 font-semibold rounded-md duration-500 hover:opacity-75"
            type="submit"
          >
            Login
          </button>
          <div className="text-center">
            Don&apos;t have an account?&nbsp;
            <Link
              href="/register"
              className="duration-500 hover:opacity-50 text-neutral-400 underline"
            >
              Register
            </Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default LoginClient;