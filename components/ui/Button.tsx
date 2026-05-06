import Link from "next/link";
import type { AnchorHTMLAttributes, ButtonHTMLAttributes } from "react";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "gold" | "green" | "outline" | "danger";
};

type LinkButtonProps = AnchorHTMLAttributes<HTMLAnchorElement> & {
  href: string;
  variant?: ButtonProps["variant"];
};

const buttonVariants = {
  gold: "border-transparent bg-gradient-to-r from-gold to-gold-light text-navy shadow-[0_6px_20px_rgba(201,162,39,0.3)] hover:-translate-y-0.5",
  green: "border-transparent bg-gradient-to-r from-success-green to-[#3daf66] text-white shadow-[0_8px_30px_rgba(45,138,78,0.35)] hover:-translate-y-0.5",
  outline: "border-gold/70 bg-gold/10 text-gold hover:bg-gold hover:text-navy",
  danger: "border-danger-red/60 bg-danger-red/10 text-red-200 hover:bg-danger-red/20"
};

const buttonBase =
  "inline-flex min-h-10 items-center justify-center rounded-lg border px-4 py-2 text-sm font-bold transition disabled:pointer-events-none disabled:opacity-45";

export function Button({ className = "", variant = "outline", ...props }: ButtonProps) {
  return <button className={`${buttonBase} ${buttonVariants[variant]} ${className}`} {...props} />;
}

export function LinkButton({ className = "", variant = "outline", href, ...props }: LinkButtonProps) {
  return <Link href={href} className={`${buttonBase} ${buttonVariants[variant]} ${className}`} {...props} />;
}
