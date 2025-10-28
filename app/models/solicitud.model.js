import { DataTypes } from "sequelize";

export default (sequelize) => {
  const Solicitud = sequelize.define("Solicitud", {
    id_solicitud: {
      type: DataTypes.INTEGER,
      primaryKey: true,
      autoIncrement: true,
    },
    id_usuario: {
      type: DataTypes.INTEGER,
      allowNull: false,
    },
    motivo: {
      type: DataTypes.STRING(255),
      allowNull: false,
    },
    estado: {
      type: DataTypes.STRING(20),
      defaultValue: "pendiente", // pendiente | resuelta | rechazada
    },
    fecha_creacion: {
      type: DataTypes.DATE,
      defaultValue: DataTypes.NOW,
    },
  }, {
    tableName: "solicitudes",
    timestamps: false,
  });

  return Solicitud;
};
