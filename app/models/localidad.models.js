import { DataTypes } from 'sequelize';

export default (sequelize) => {
  return sequelize.define('localidades', {
    id_localidad: {
      type: DataTypes.INTEGER,
      autoIncrement: true,
      primaryKey: true
    },
    nombre: {
      type: DataTypes.STRING(50),
      allowNull: false,
      unique: true
    }
  }, {
    tableName: 'localidades',
    timestamps: false
  });
};