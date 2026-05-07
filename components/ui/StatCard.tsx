export function StatCard({ value, label }: { value: number | string; label: string }) {
  return (
    <div className="simulator-stat-card">
      <div className="simulator-stat-num">{value}</div>
      <div className="simulator-stat-label">{label}</div>
    </div>
  );
}
