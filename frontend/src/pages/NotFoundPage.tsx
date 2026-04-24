import { Link } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/button";
import { useLanguage } from "../contexts/LanguageContext";
import type { PageType } from "../App";
import { PATHS } from "../navigation/paths";

export function NotFoundPage({
  onNavigate,
}: {
  onNavigate?: (page: PageType) => void;
}) {
  const { locale } = useLanguage();
  return (
    <PageLayout onNavigate={onNavigate}>
      <div className="mx-auto max-w-lg px-4 py-32 text-center">
        <h1 className="text-4xl font-bold text-foreground">404</h1>
        <p className="mt-4 text-muted-foreground">
          {locale === "tr" ? "Sayfa bulunamadı." : "This page could not be found."}
        </p>
        <Button asChild className="mt-8">
          <Link to={PATHS.home}>
            {locale === "tr" ? "Ana sayfa" : "Home"}
          </Link>
        </Button>
      </div>
    </PageLayout>
  );
}
