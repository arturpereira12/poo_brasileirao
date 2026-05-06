export function StatCard({ value, label }: { value: number | string; label: string }) {
  return (
    <div className="java-stat-card">
      <div className="java-stat-num">{value}</div>
      <div className="java-stat-label">{label}</div>
    </div>
  );
}
