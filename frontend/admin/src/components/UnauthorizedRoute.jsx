import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";

import Loading from "./Loading/Loading";

import { validateToken } from "../services/auth";

export default function UnauthorizedRoute({ children }) {
    const navigate = useNavigate();
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        (async () => {
            try {
                await validateToken();
                navigate("/blog");
            } catch {
                localStorage.removeItem("token");
                setLoading(false);
            }
        })();
    }, []);

    if (loading) return <Loading />;
    return children;
}
