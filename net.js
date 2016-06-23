import Rx from 'rxjs';

function Net() {

  this.fetchJson = (data) => fetch(data).then((response) => response.json());

}

module.exports = new Net();
