import { useEffect } from "react";
import { useLocation } from "react-router-dom";

/**
 * Route değişince sayfanın en üstüne kayar (tema layout davranışı).
 */
export function ScrollToTop() {
  const { pathname } = useLocation();
  useEffect(() => {
    window.scrollTo(0, 0);
  }, [pathname]);
  return null;
}
