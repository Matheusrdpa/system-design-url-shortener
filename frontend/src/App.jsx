import { useState } from "react";
import { Check, Copy, Link2, LoaderCircle, Sparkles } from "lucide-react";

const apiUrl = (import.meta.env.VITE_API_URL || "http://localhost:8080").replace(/\/$/, "");

const steps = [
  "Cole sua URL original",
  "Gere um link curto em segundos",
  "Copie e use onde quiser"
];

export default function App() {
  const [originalUrl, setOriginalUrl] = useState("");
  const [shortUrl, setShortUrl] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);
  const [copied, setCopied] = useState(false);

  async function handleSubmit(event) {
    event.preventDefault();

    if (!originalUrl.trim()) {
      setError("Informe uma URL para encurtar.");
      setShortUrl("");
      return;
    }

    setLoading(true);
    setError("");
    setCopied(false);

    try {
      const response = await fetch(`${apiUrl}/url`, {
        method: "POST",
        headers: {
          "Content-Type": "application/json"
        },
        body: JSON.stringify({ originalUrl: originalUrl.trim() })
      });

      const contentType = response.headers.get("content-type") || "";
      const payload = contentType.includes("application/json")
        ? await response.json()
        : await response.text();

      if (!response.ok) {
        const message =
          typeof payload === "string"
            ? payload
            : payload.error || "Nao foi possivel encurtar a URL.";

        throw new Error(message);
      }

      setShortUrl(typeof payload === "string" ? payload : "");
    } catch (submitError) {
      setError(submitError.message || "Nao foi possivel encurtar a URL.");
      setShortUrl("");
    } finally {
      setLoading(false);
    }
  }

  async function handleCopy() {
    if (!shortUrl) {
      return;
    }

    try {
      await navigator.clipboard.writeText(shortUrl);
      setCopied(true);
      window.setTimeout(() => setCopied(false), 2000);
    } catch {
      setError("Nao foi possivel copiar o link.");
    }
  }

  return (
    <main className="min-h-screen bg-[radial-gradient(circle_at_top,_rgba(253,224,71,0.35),_transparent_32%),linear-gradient(135deg,_#f8fafc_0%,_#eef2ff_45%,_#e0f2fe_100%)] text-slate-950">
      <section className="mx-auto flex min-h-screen w-full max-w-6xl flex-col justify-center gap-10 px-6 py-10 lg:flex-row lg:items-center lg:gap-16">
        <div className="max-w-xl space-y-6">
          <div className="inline-flex items-center gap-2 rounded-full border border-white/60 bg-white/70 px-4 py-2 text-sm font-medium text-slate-700 shadow-sm backdrop-blur">
            <Sparkles size={16} />
            Simple url shortener
          </div>

          <div className="space-y-4">
            <h1 className="font-display text-5xl leading-tight tracking-tight text-balance sm:text-6xl">
              Links curtos com apenas um clique.
            </h1>
          </div>

          <div className="grid gap-3 sm:grid-cols-3">
            {steps.map((step, index) => (
              <article
                key={step}
                className="rounded-2xl border border-slate-200/80 bg-white/65 p-4 shadow-[0_12px_30px_-18px_rgba(15,23,42,0.45)] backdrop-blur"
              >
                <p className="text-sm text-slate-500">0{index + 1}</p>
                <p className="mt-2 text-sm font-medium text-slate-800">{step}</p>
              </article>
            ))}
          </div>
        </div>

        <div className="w-full max-w-xl">
          <div className="rounded-[2rem] border border-white/70 bg-white/80 p-6 shadow-[0_30px_80px_-32px_rgba(15,23,42,0.4)] backdrop-blur sm:p-8">
            <div className="mb-8 flex items-center gap-3">
              <div className="flex h-12 w-12 items-center justify-center rounded-2xl bg-slate-950 text-white">
                <Link2 size={20} />
              </div>
              <div>
                <p className="text-sm font-medium text-slate-500">Nova URL curta</p>
                <h2 className="text-2xl font-semibold tracking-tight text-slate-900">
                  Criar link
                </h2>
              </div>
            </div>

            <form className="space-y-4" onSubmit={handleSubmit}>
              <label className="block space-y-2">
                <span className="text-sm font-medium text-slate-700">URL original</span>
                <input
                  type="url"
                  value={originalUrl}
                  onChange={(event) => setOriginalUrl(event.target.value)}
                  placeholder="https://example.com/path"
                  className="w-full rounded-2xl border border-slate-200 bg-slate-50 px-4 py-4 text-base text-slate-900 outline-none transition focus:border-slate-950 focus:bg-white"
                />
              </label>

              <button
                type="submit"
                disabled={loading}
                className="inline-flex w-full items-center justify-center gap-2 rounded-2xl bg-slate-950 px-5 py-4 text-base font-medium text-white transition hover:bg-slate-800 disabled:cursor-not-allowed disabled:bg-slate-400"
              >
                {loading ? <LoaderCircle className="animate-spin" size={18} /> : <Link2 size={18} />}
                {loading ? "Gerando..." : "Encurtar URL"}
              </button>
            </form>

            <div className="mt-6 min-h-28 rounded-2xl border border-dashed border-slate-200 bg-slate-50/80 p-4">
              {shortUrl ? (
                <div className="space-y-4">
                  <p className="text-sm font-medium text-slate-500">Link gerado</p>
                  <div className="flex flex-col gap-3 sm:flex-row">
                    <a
                      href={shortUrl}
                      target="_blank"
                      rel="noreferrer"
                      className="min-w-0 flex-1 truncate rounded-2xl bg-white px-4 py-3 text-sm font-medium text-sky-700 shadow-sm"
                    >
                      {shortUrl}
                    </a>
                    <button
                      type="button"
                      onClick={handleCopy}
                      className="inline-flex items-center justify-center gap-2 rounded-2xl border border-slate-200 bg-white px-4 py-3 text-sm font-medium text-slate-700 transition hover:border-slate-300 hover:bg-slate-100"
                    >
                      {copied ? <Check size={16} /> : <Copy size={16} />}
                      {copied ? "Copiado" : "Copiar"}
                    </button>
                  </div>
                </div>
              ) : (
                <div className="flex h-full flex-col justify-center">
                  <p className="text-sm font-medium text-slate-500">Resultado</p>
                  <p className="mt-2 text-sm leading-6 text-slate-500">
                    O link encurtado aparece aqui assim que a requisicao for concluida.
                  </p>
                </div>
              )}

              {error ? <p className="mt-4 text-sm font-medium text-rose-600">{error}</p> : null}
            </div>
          </div>
        </div>
      </section>
    </main>
  );
}
