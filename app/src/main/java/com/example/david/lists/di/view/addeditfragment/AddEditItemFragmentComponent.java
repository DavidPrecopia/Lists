package com.example.david.lists.di.view.addeditfragment;

import android.app.Application;

import androidx.fragment.app.Fragment;

import com.example.david.lists.ui.addedit.item.AddEditItemFragment;

import javax.inject.Named;

import dagger.BindsInstance;
import dagger.Component;

import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.ID;
import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.TITLE;
import static com.example.david.lists.di.view.addeditfragment.AddEditNamedConstants.USER_LIST_ID;

@AddEditFragmentScope
@Component(modules = {
        AddEditItemFragmentModule.class,
        AddEditFragmentCommonModule.class
})
public interface AddEditItemFragmentComponent {
    void inject(AddEditItemFragment fragment);

    @Component.Builder
    interface Builder {
        AddEditItemFragmentComponent build();

        @BindsInstance
        Builder application(Application application);

        @BindsInstance
        Builder fragment(Fragment fragment);

        @BindsInstance
        Builder id(@Named(ID) String id);

        @BindsInstance
        Builder title(@Named(TITLE) String title);

        @BindsInstance
        Builder userListId(@Named(USER_LIST_ID) String userListId);
    }
}