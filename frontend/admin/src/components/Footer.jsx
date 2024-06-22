export default function Footer({ absolute }) {
    return (
        <footer
            className={`flex 
                  flex-col
                  items-center
                  gap-3
                  text-[10px]
                  ${
                      absolute
                          ? "absolute bottom-4 left-0 right-0"
                          : "mt-16 pb-4"
                  }
                `}
        >
            <p>&copy; 2023 Michael Yi, All Rights Reserved.</p>
        </footer>
    );
}
