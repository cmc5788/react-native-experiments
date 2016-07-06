'use strict';

import _ from 'lodash';

function DetailPagePresenter() {

  this.init = () => {
    console.log(`DetailPagePresenter init ${JSON.stringify(this.view.state)}`);

    this.view.sendBatch([
      {
        setLabelText: {
          id: this.fromHome ?
            'x_from_home' :
            'x_not_from_home',
          args: [ this.tagExtras ]
        }
      },
      { setButtonText: { id: 'collapse_nav_stack' } },
      { setButtonColor: '#00FF00' },
    ]);
  };

  this.save = () => {
    console.log('DetailPagePresenter save');
  };

  this.destroy = () => {
    console.log('DetailPagePresenter destroy');
    this.unsub('collapseStackSub');
  };

  this.back = () => {
    console.log('DetailPagePresenter back');
  };

  this.initFromHome = () => {
    this.fromHome = true;
  };

  this.labelClicked = () => {
    console.log('DetailPagePresenter labelClicked');
    this.nav.navigate(this.nav.DETAIL_PAGE, `${this.tagExtras}x`);
  };

  this.buttonClicked = () => {
    console.log('DetailPagePresenter buttonClicked');

    if (_.get(this, 'collapseStackSub.isUnsubscribed', true)) {
      this.sub('collapseStackSub',
        this.nav.stackObs().flatMap(stack => this.nav.setStackObs(
          _.concat(_.remove(
            stack, s => _.startsWith(s, this.nav.HOME_PAGE)),
            this.tag))),
          success => console.log(`collapseStackSub success`),
          err => console.log(err)
        );
    }
  };
}

module.exports = DetailPagePresenter;
