import type { Metadata } from "next";
import { Inter } from "next/font/google";
import "./globals.css";
import Providers from "../components/Providers";

interface Props {
  children: React.ReactNode;
}

const inter = Inter({ subsets: ["latin"] });

export const metadata: Metadata = {
  title: "Michael Yi",
  description: "© 2023 Michael Yi, All Rights Reserved.",
  icons: {
    icon: "/Michael.png",
  },
  viewport: {
    width: "device-width",
    initialScale: 1,
  },
};

const RootLayout: React.FC<Props> = ({ children }) => {
  return (
    <html lang="en">
      <body className={inter.className}>
        <Providers>{children}</Providers>
      </body>
    </html>
  );
};

export default RootLayout;