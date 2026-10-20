"use client";

import { MapContainer, TileLayer, Marker, Popup, useMapEvents } from "react-leaflet";
import L from "leaflet";
import "leaflet/dist/leaflet.css";

import markerIcon2x from "leaflet/dist/images/marker-icon-2x.png";
import markerIcon from "leaflet/dist/images/marker-icon.png";
import markerShadow from "leaflet/dist/images/marker-shadow.png";

// Corrige o caminho dos ícones padrão do Leaflet quando empacotado pelo Next.js
delete L.Icon.Default.prototype._getIconUrl;
L.Icon.Default.mergeOptions({
  iconRetinaUrl: markerIcon2x.src,
  iconUrl: markerIcon.src,
  shadowUrl: markerShadow.src,
});

// Centro aproximado de Medianeira - PR
const MEDIANEIRA_CENTER = [-25.2955, -54.0936];

const TYPE_LABELS = {
  PARTNER_COLLECTION_POINT: "Ponto de coleta parceiro",
  ASSISTANCE_ENTITY_HQ: "Sede de entidade assistencial",
};

function ClickHandler({ onMapClick }) {
  useMapEvents({
    click(e) {
      if (onMapClick) {
        onMapClick(e.latlng.lat, e.latlng.lng);
      }
    },
  });
  return null;
}

function buildAddress(point) {
  const parts = [
    [point.street, point.number].filter(Boolean).join(", "),
    point.neighborhood,
    [point.city, point.state].filter(Boolean).join(" - "),
    point.zipCode,
  ].filter(Boolean);
  return parts.join(" • ");
}

export default function CollectionPointsMap({
  points = [],
  center = MEDIANEIRA_CENTER,
  zoom = 14,
  height = "420px",
  onMapClick = null,
  selectedPosition = null,
}) {
  return (
    <MapContainer
      center={center}
      zoom={zoom}
      scrollWheelZoom={true}
      style={{ height, width: "100%", borderRadius: "8px" }}
    >
      <TileLayer
        attribution='&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
        url="https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"
      />

      {onMapClick && <ClickHandler onMapClick={onMapClick} />}

      {points
        .filter((p) => p.latitude != null && p.longitude != null)
        .map((p) => (
          <Marker key={p.collectionPointId} position={[p.latitude, p.longitude]}>
            <Popup>
              <strong>{p.name}</strong>
              <br />
              {TYPE_LABELS[p.type] || p.type}
              <br />
              {buildAddress(p)}
              {p.operatingHours && (
                <>
                  <br />
                  <em>Horário: {p.operatingHours}</em>
                </>
              )}
              {p.phone && (
                <>
                  <br />
                  Telefone: {p.phone}
                </>
              )}
            </Popup>
          </Marker>
        ))}

      {selectedPosition && selectedPosition.lat != null && selectedPosition.lng != null && (
        <Marker position={[selectedPosition.lat, selectedPosition.lng]}>
          <Popup>Local selecionado</Popup>
        </Marker>
      )}
    </MapContainer>
  );
}
