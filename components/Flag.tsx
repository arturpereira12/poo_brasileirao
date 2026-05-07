import { cn } from "@/lib/utils";

const FIFA_TO_FLAG_ICONS_CODE: Record<string, string> = {
  ALG: "dz",
  ARG: "ar",
  AUS: "au",
  AUT: "at",
  BEL: "be",
  BIH: "ba",
  BRA: "br",
  CAN: "ca",
  CIV: "ci",
  COD: "cd",
  COL: "co",
  CPV: "cv",
  CRO: "hr",
  CUW: "cw",
  CZE: "cz",
  ECU: "ec",
  EGY: "eg",
  ENG: "gb-eng",
  ESP: "es",
  FRA: "fr",
  GER: "de",
  GHA: "gh",
  HAI: "ht",
  IRN: "ir",
  IRQ: "iq",
  JOR: "jo",
  JPN: "jp",
  KOR: "kr",
  KSA: "sa",
  MAR: "ma",
  MEX: "mx",
  NED: "nl",
  NOR: "no",
  NZL: "nz",
  PAN: "pa",
  PAR: "py",
  POR: "pt",
  QAT: "qa",
  RSA: "za",
  SCO: "gb-sct",
  SEN: "sn",
  SUI: "ch",
  SWE: "se",
  TUN: "tn",
  TUR: "tr",
  URU: "uy",
  USA: "us",
  UZB: "uz",
};

export function getFlagIconCode(countryCode?: string): string | null {
  if (!countryCode) return null;
  return FIFA_TO_FLAG_ICONS_CODE[countryCode.toUpperCase()] ?? countryCode.toLowerCase();
}

export function Flag({ countryCode, label, className }: { countryCode?: string; label?: string; className?: string }) {
  const flagIconCode = getFlagIconCode(countryCode);

  if (!flagIconCode) {
    return <span aria-hidden="true" className={cn("inline-block aspect-[4/3] bg-white/10", className)} />;
  }

  return (
    <span
      aria-label={label ? `${label} flag` : undefined}
      role={label ? "img" : undefined}
      className={cn("fi rounded-[2px] shadow-sm", `fi-${flagIconCode}`, className)}
    />
  );
}
