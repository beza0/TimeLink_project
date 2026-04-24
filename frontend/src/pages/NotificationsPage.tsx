import { useCallback, useEffect, useMemo, useState } from "react";
import { Link, useSearchParams } from "react-router-dom";
import { PageLayout } from "../components/layout/PageLayout";
import { Button } from "../components/ui/button";
import { Checkbox } from "../components/ui/checkbox";
import { Label } from "../components/ui/label";
import { useLanguage } from "../contexts/LanguageContext";
import { useAuth } from "../contexts/AuthContext";
import type { PageType } from "../App";
import { PATHS } from "../navigation/paths";
import {
  fetchNotifications,
  isNotificationUnread,
  markNotificationRead,
  type NotificationDto,
} from "../api/notifications";
import { apiErrorDisplayMessage } from "../api/client";

export function NotificationsPage({
  onNavigate,
}: {
  onNavigate?: (page: PageType) => void;
}) {
  const { t, locale } = useLanguage();
  const p = t.notificationsPage;
  const { token } = useAuth();
  const [searchParams, setSearchParams] = useSearchParams();
  const [list, setList] = useState<NotificationDto[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState<Set<string>>(new Set());
  const unreadOnly = searchParams.get("unread") === "1";

  const load = useCallback(async () => {
    if (!token) {
      setList([]);
      return;
    }
    setLoading(true);
    setError(null);
    try {
      const rows = await fetchNotifications(token);
      setList(rows);
    } catch (e) {
      setError(apiErrorDisplayMessage(e, t.common.loading));
      setList([]);
    } finally {
      setLoading(false);
    }
  }, [t.common.loading, token]);

  useEffect(() => {
    void load();
  }, [load]);

  const visible = useMemo(() => {
    if (!unreadOnly) return list;
    return list.filter((n) => isNotificationUnread(n));
  }, [list, unreadOnly]);

  const toggleUnreadFilter = (on: boolean) => {
    setSearchParams(on ? { unread: "1" } : {});
  };

  const toggleSelect = (id: string) => {
    setSelected((prev) => {
      const n = new Set(prev);
      if (n.has(id)) n.delete(id);
      else n.add(id);
      return n;
    });
  };

  const selectAllVisible = () => {
    setSelected(new Set(visible.map((n) => n.id)));
  };

  const markSelectedRead = async () => {
    if (!token || selected.size === 0) return;
    for (const id of selected) {
      try {
        await markNotificationRead(token, id);
      } catch {
        /* devam */
      }
    }
    setSelected(new Set());
    void load();
  };

  return (
    <PageLayout onNavigate={onNavigate}>
      <div className="mx-auto max-w-2xl px-4 pb-12 pt-20 sm:px-6">
        <div className="mb-6 flex flex-col gap-4 sm:flex-row sm:items-center sm:justify-between">
          <h1 className="text-2xl font-bold text-foreground">{p.title}</h1>
          <div className="flex flex-wrap items-center gap-2">
            <div className="flex items-center gap-2">
              <Checkbox
                id="unread-only"
                checked={unreadOnly}
                onCheckedChange={(c) => toggleUnreadFilter(c === true)}
              />
              <Label htmlFor="unread-only" className="cursor-pointer text-sm">
                {p.unreadOnly}
              </Label>
            </div>
            <Button
              type="button"
              size="sm"
              variant="outline"
              onClick={selectAllVisible}
              disabled={visible.length === 0}
            >
              {p.selectAllVisible}
            </Button>
            <Button
              type="button"
              size="sm"
              onClick={() => void markSelectedRead()}
              disabled={selected.size === 0}
            >
              {p.markSelectedRead} ({selected.size})
            </Button>
          </div>
        </div>
        {error ? <p className="text-sm text-destructive">{error}</p> : null}
        {loading ? (
          <p className="text-muted-foreground">{t.common.loading}</p>
        ) : visible.length === 0 ? (
          <p className="text-muted-foreground">{p.empty}</p>
        ) : (
          <ul className="space-y-2">
            {visible.map((n) => {
              const u = isNotificationUnread(n);
              return (
                <li
                  key={n.id}
                  className={`flex gap-3 rounded-lg border p-3 ${
                    u ? "border-l-4 border-l-primary bg-primary/5" : "bg-muted/30"
                  }`}
                >
                  <Checkbox
                    checked={selected.has(n.id)}
                    onCheckedChange={() => toggleSelect(n.id)}
                    aria-label="select"
                  />
                  <div className="min-w-0 flex-1">
                    <p className="font-medium text-foreground">{n.title}</p>
                    <p className="text-sm text-muted-foreground whitespace-pre-wrap">
                      {n.body}
                    </p>
                    {n.exchangeRequestId ? (
                      <Link
                        to={`${PATHS.messages}?open=${encodeURIComponent(n.exchangeRequestId!)}`}
                        className="mt-2 inline-block text-sm text-primary"
                      >
                        {t.nav.goToMessages} →
                      </Link>
                    ) : null}
                    <p className="mt-1 text-xs text-muted-foreground">
                      {new Date(n.createdAt).toLocaleString(
                        locale === "tr" ? "tr-TR" : "en-US",
                        { dateStyle: "short", timeStyle: "short" },
                      )}
                    </p>
                  </div>
                </li>
              );
            })}
          </ul>
        )}
      </div>
    </PageLayout>
  );
}
