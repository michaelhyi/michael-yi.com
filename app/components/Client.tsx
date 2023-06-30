"use client";

import { ReactNode, useEffect, useState } from "react";

interface Props {
  children: ReactNode;
}

const Client: React.FC<Props> = ({ children }) => {
  const [mounted, setMounted] = useState(false);

  useEffect(() => {
    setMounted(true);
  }, []);

  return <>{children}</>;
};

export default Client;
