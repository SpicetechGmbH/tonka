import { dtsAxios } from "./axios";

export default {
  get() {
    const url = `/startMessage`;
    return new Promise((resolve, reject) => {
      dtsAxios.get(url)
        .then((response) => {
          resolve(response);
        })
        .catch((error) => {
          reject(error);
        });
    });
  }
}
