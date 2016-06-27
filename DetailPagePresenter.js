'use strict';

function DetailPagePresenter() {

  this.labelClicked = () => {
    console.log('DetailPagePresenter labelClicked');
    this.nav.navigate(this.nav.DETAIL_PAGE, this.tagExtras + 'x', 'meta');
  };

  this.goBackClicked = () => {
    console.log('DetailPagePresenter goBackClicked');
    this.nav.goBack();
  };

  this.init = () => {
    console.log('DetailPagePresenter init');
    this.view.send({ setLabelText: this.tagExtras });
  };

  this.back = () => {
    console.log('DetailPagePresenter back');
  };

  this.destroy = () => {
    console.log('DetailPagePresenter destroy');
  };
}

module.exports = DetailPagePresenter;
