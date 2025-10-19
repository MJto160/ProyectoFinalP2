import { DataTypes } from 'sequelize';

export default (sequelize) => {
  return sequelize.define('Partido', {
    id_partido: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true
    },
    equipo_local: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    equipo_visitante: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    fecha_partido: {
      type: DataTypes.DATE,
      allowNull: false
    },
    estadio: {
      type: DataTypes.STRING(100),
      allowNull: false
    },
    estado: {
      type: DataTypes.STRING(20),
      allowNull: false
    }
  }, {
    tableName: 'partidos',
    timestamps: false
  });
};
